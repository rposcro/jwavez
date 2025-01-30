package com.rposcro.jwavez.tools.shell.commands.node;

import com.jwavez.jwavez.products.model.Parameter;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeParameterService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

import java.text.ParseException;

@ShellComponent
@Command(group = CommandGroup.NODE)
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

    @Command(command = "param print", alias = "pp", description = "Print parameter(s)")
    @CommandAvailability(provider = "nodeAvailability")
    public String printParametersDetails(
            @Option(longNames = "param-numbers", shortNames = 'p', required = true) String paramNumbersRange,
            @Option(longNames = "verbose", shortNames = 'v', defaultValue = "false") boolean verbose
    ) {
        try {
            int[] paramNumbers = parseParamNumbersArgument(paramNumbersRange);
            StringBuffer paramDetails = new StringBuffer();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
            for (int number : paramNumbers) {
                paramDetails.append(verbose ? formatParamVerboseLine(nodeInformation, number) : formatParamValueLine(nodeInformation, number));
                paramDetails.append('\n');
            }
            return paramDetails.toString();
        } catch (ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }

    @Command(command = "param learn", alias = "pl", description = "Learn about parameter(s) value")
    @CommandAvailability(provider = {"nodeAvailability", "dongleAvailability"})
    public String fetchParametersValues(
        @Option(longNames = "param-numbers", shortNames = 'p', required = true) String paramNumbersRange
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

    @Command(command = "param set", alias = "ps", description = "Set parameter value")
    @CommandAvailability(provider = {"nodeAvailability", "dongleAvailability"})
    public String setParameterValue(
        @Option(longNames = "param-number", shortNames = 'p', required = true) int paramNumber,
        @Option(longNames = "param-value", shortNames = 'w', required = true) int paramValue
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();

        if (!productsSpecificationsProvider.hasParameter(nodeId, paramNumber)) {
            return "Parameter " + paramNumber + " is not known for node " + nodeId;
        }

        boolean success = nodeParameterService.sendParameterValue(nodeId, paramNumber, paramValue);
        if (success) {
            return String.format("Parameter %s of node %s is now %04x", paramNumber, nodeId, paramValue);
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
            line = String.format("Param %s: %s (x%0" + (parameter.getBitSize() / 4) + "x)", paramNumber, paramValue, paramValue);
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
            line = String.format("Param %s:\n  size in bits: %s\n  memo: %s\n  value: %s",
                parameter.getNumber(),
                parameter.getBitSize(),
                parameter.getDescription(),
                paramValue != null ? String.format("%s (x%0" + (parameter.getBitSize() / 4) + "x)", paramValue, paramValue) : "<value unknown>"
            );
        }

        return line;
    }
}
