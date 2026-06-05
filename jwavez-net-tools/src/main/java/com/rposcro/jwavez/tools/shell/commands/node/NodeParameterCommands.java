package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.products.model.Parameter;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeParameterService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import java.text.ParseException;

import static java.lang.String.format;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
public class NodeParameterCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeParameterService nodeParameterService;

    @Autowired
    private NumberRangeParser numberRangeParser;

    @Autowired
    private ProductsSpecificationsProvider productsSpecificationsProvider;

    @Command(name = "param print", alias = "pp", description = "Print parameter(s)",
        availabilityProvider = "nodeAvailability")
    public String printParametersDetails(
            @Argument(index = 0, description = "Parameter number(s) to print information about", defaultValue = "*") String paramNumbersRange,
            @Option(shortName = 'v', longName = "verbose", defaultValue = "false") boolean verbose
    ) {
        try {
            int[] paramNumbers = parseParamNumbersArgument(paramNumbersRange);
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
            int nodeId = nodeInformation.getNodeId();
            StringBuffer paramDetails = new StringBuffer(format("Parameters of node %s (0x%02x):\n\n", nodeId, nodeId));
            for (int number : paramNumbers) {
                paramDetails.append(verbose ? formatParamVerboseLine(nodeInformation, number) : formatParamValueLine(nodeInformation, number));
                paramDetails.append('\n');
            }
            return paramDetails.toString();
        } catch (ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }

    @Command(name = "param learn", alias = "pl", description = "Learn about parameter(s) value",
        availabilityProvider = "dongleRepositoryNodeAvailability")
    public String fetchParametersValues(
        @Argument(index = 0, defaultValue = "*", description = "Parameter number(s) or * to learn all") String paramNumbersRange
    ) throws SerialException {
        try {
            int[] paramNumbers = parseParamNumbersArgument(paramNumbersRange);
            int nodeId = nodeScopeContext.getCurrentNodeId();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

            StringBuffer paramDetails = new StringBuffer();
            for (int number : paramNumbers) {
                nodeParameterService.fetchParameterValue(nodeId, number);
                paramDetails.append(formatParamVerboseLine(nodeInformation, number));
                paramDetails.append('\n');
            }
            return paramDetails.toString();

        } catch (ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }

    @Command(name = "param set", alias = "ps", description = "Set parameter value",
            availabilityProvider = "dongleRepositoryNodeAvailability")
    public String setParameterValue(
        @Argument(index = 0, description = "Parameter number to set value for") int paramNumber,
        @Argument(index = 1, description = "New parameter value to set") int paramValue
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();

        if (!productsSpecificationsProvider.hasParameter(nodeId, paramNumber)) {
            return "Parameter " + paramNumber + " is not known for node " + nodeId;
        }

        boolean success = nodeParameterService.sendParameterValue(nodeId, paramNumber, paramValue);
        if (success) {
            return format("Parameter %s of node %s is now %04x", paramNumber, nodeId, paramValue);
        } else {
            return "Something went wrong and parameter value has not been changed";
        }
    }

    private int[] parseParamNumbersArgument(String paramNumbersRange) throws ParseException {
        if (paramNumbersRange != null && !"*".equals(paramNumbersRange)) {
            return numberRangeParser.parseNumberRange(paramNumbersRange);
        } else {
            return productsSpecificationsProvider.findParameterNumbers(nodeScopeContext.getCurrentNodeId());
        }
    }

    private String formatParamValueLine(NodeInformation nodeInformation, int paramNumber) {
        Parameter parameter = productsSpecificationsProvider.findParameter(nodeInformation, paramNumber);
        Long paramValue = nodeInformation.getParametersInformation().findParameterValue(paramNumber);
        String line;

        if (parameter == null) {
            line = "Param " + paramNumber + ": <param unknown>";
        } else if (paramValue == null) {
            line = "Param " + paramNumber + ": <value unknown>";
        } else {
            line = format("Param %s: %s (x%0" + (parameter.getBitSize() / 4) + "x)", paramNumber, paramValue, paramValue);
        }
        return line;
    }

    private String formatParamVerboseLine(NodeInformation nodeInformation, int paramNumber) {
        Parameter parameter = productsSpecificationsProvider.findParameter(nodeInformation, paramNumber);
        Long paramValue = nodeInformation.getParametersInformation().findParameterValue(paramNumber);
        String line;

        if (parameter == null) {
            line = "Param " + paramNumber + ": <param unknown>";
        } else {
            line = format("Param %s:\n  size in bits: %s\n  name: %s\n  value: %s",
                parameter.getNumber(),
                parameter.getBitSize(),
                parameter.getName(),
                paramValue != null ? format("%s (x%0" + (parameter.getBitSize() / 4) + "x)", paramValue, paramValue) : "<value unknown>"
            );
        }

        return line;
    }
}
