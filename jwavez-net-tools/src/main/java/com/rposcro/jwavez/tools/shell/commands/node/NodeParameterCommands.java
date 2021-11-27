package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.models.ParameterMeta;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import com.rposcro.jwavez.tools.shell.services.NodeParameterService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeParameterCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeParameterService nodeParameterService;

    @Autowired
    private NumberRangeParser numberRangeParser;

    @ShellMethod(value = "Define node parameter", key = { "param define", "pd" })
    public String defineParameter(
            @ShellOption(value = { "--param-number", "-pn" }) int paramNumber,
            @ShellOption(value = { "--param-memo", "-memo" }) String paramMemo,
            @ShellOption(value = { "--size-in-bytes", "-sib" }) int sizeInBytes
    ) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        int sizeInBits = sizeInBytes * 8;
        nodeParameterService.updateOrCreateMeta(nodeId, paramNumber, sizeInBits, paramMemo);
        return String.format("Parameter defined:\n  node id: %s\n  number: %s\n  size in bits: %s\n  memo: %s"
                , nodeId, paramNumber, sizeInBits, paramMemo);
    }

    @ShellMethod(value = "Clone node parameters definitions from given node to current", key = { "param clone" })
    public String cloneParameters(
            @ShellOption(value = { "--node-id", "-id" }) int sourceNodeId
    ) {
        int currentNodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation currentNode = nodeInformationCache.getNodeDetails(currentNodeId);
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);

        if (sourceNode != null) {
            if (nodeInformationService.nodesMatch(currentNode, sourceNode)) {
                nodeParameterService.cloneParametersMetas(sourceNodeId, currentNodeId);
                return "Parameters definitions cloned to current node";
            } else {
                return "Node " + sourceNodeId + " doesn't match the current one, cannot clone from it";
            }
        } else {
            return "Node " + sourceNodeId + " is unknown, cannot clone from it";
        }
    }

    @ShellMethod(value = "Delete node parameter definition", key = { "param delete" })
    public String deleteParameterDefinitionAndValue(
            @ShellOption(value = { "--param-number", "-pn" }) int paramNumber
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation.getParametersInformation().removeParameterMeta(paramNumber) != null) {
            nodeInformation.getParametersInformation().removeParameterValue(paramNumber);
            return "Parameter " + paramNumber + " deleted";
        } else {
            return "Unknown parameter " + paramNumber;
        }
    }

    @ShellMethod(value = "Print parameter(s)", key = { "param print", "pp" })
    public String printParametersDetails(
            @ShellOption(value = { "--param-numbers", "-pns" }, defaultValue = ShellOption.NULL) String paramNumbersRange,
            @ShellOption(defaultValue = "false") boolean verbose
    ) {
        try {
            int[] paramNumbers = parseParamNumbersArgument(paramNumbersRange);
            StringBuffer paramDetails = new StringBuffer();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
            for (int number: paramNumbers) {
                paramDetails.append(verbose ? formatParamVerboseLine(nodeInformation, number) :  formatParamValueLine(nodeInformation, number));
                paramDetails.append('\n');
            }
            return paramDetails.toString();
        } catch(ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }

    @ShellMethod(value = "Learn about parameter(s) value", key = { "param learn", "pl" })
    public String fetchParametersValues(
            @ShellOption(value = { "--param-numbers", "-pns" }, defaultValue = ShellOption.NULL) String paramNumbersRange
    ) throws SerialException {
        try {
            int[] paramNumbers = parseParamNumbersArgument(paramNumbersRange);
            int nodeId = nodeScopeContext.getCurrentNodeId();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

            StringBuffer paramDetails = new StringBuffer();
            for (int number: paramNumbers) {
                nodeParameterService.fetchParameterValue(nodeId, number);
                paramDetails.append(formatParamVerboseLine(nodeInformation, number));
                paramDetails.append('\n');
            }
            return paramDetails.toString();

        } catch(ParseException e) {
            return "Cannot parse argument: " + paramNumbersRange;
        }
    }

    @ShellMethod(value = "Set parameter value", key = { "param set", "ps" })
    public String setParameterValue(
            @ShellOption(value = { "--param-number", "-pn" }) int paramNumber,
            @ShellOption(value = { "--param-value", "-pv" }) long paramValue
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

        if (!nodeInformation.getParametersInformation().isParameterDefined(paramNumber)) {
            return "Parameter " + paramNumber + " is not known for node " + nodeId;
        }

        boolean success = nodeParameterService.sendParameterValue(nodeId, paramNumber, paramValue);
        if (success) {
            return String.format("Parameter %s of node %s is now %04x", paramNumber, nodeId, paramValue);
        } else {
            return "Something went wrong and parameter value has not been changed";
        }
    }

    @ShellMethodAvailability(value = { "param learn", "param set" })
    public Availability checkRemoteAvailability() {
        if (!nodeScopeContext.isAnyNodeSelected()) {
            return Availability.unavailable("No node is selected in the working context, try to select or fetch one");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    @ShellMethodAvailability({ "param clone", "param define", "param delete", "param print" })
    public Availability checkLocalAvailability() {
        return nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one");
    }

    private int[] parseParamNumbersArgument(String paramNumbersRange) throws ParseException {
        if (paramNumbersRange != null && !"*".equals(paramNumbersRange)) {
            return numberRangeParser.parseNumberRange(paramNumbersRange);
        } else {
            return nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId())
                    .getParametersInformation().getParameterMetas().stream()
                    .mapToInt(ParameterMeta::getNumber)
                    .toArray();
        }
    }

    private String formatParamValueLine(NodeInformation nodeInformation, int paramNumber) {
        ParameterMeta parameterMeta = nodeInformation.getParametersInformation().findParameterMeta(paramNumber);
        Long paramValue = nodeInformation.getParametersInformation().findParameterValue(paramNumber);
        String line;

        if (parameterMeta == null) {
            line = "Param " + paramNumber + ": <param unknown>";
        } else if (paramValue == null) {
            line = "Param " + paramNumber + ": <value unknown>";
        } else {
            line = String.format("Param %s: %0" + (parameterMeta.getSizeInBits() / 4) + "X", paramNumber, paramValue);
        }
        return line;
    }

    private String formatParamVerboseLine(NodeInformation nodeInformation, int paramNumber) {
        ParameterMeta parameterMeta = nodeInformation.getParametersInformation().findParameterMeta(paramNumber);
        Long paramValue = nodeInformation.getParametersInformation().findParameterValue(paramNumber);
        String line;

        if (parameterMeta == null) {
            line = "Param " + paramNumber + ": <param unknown>";
        } else {
            line = String.format("Param %s:\n  size in bits: %s\n  memo: %s\n  value: %s",
                    parameterMeta.getNumber(),
                    parameterMeta.getSizeInBits(),
                    parameterMeta.getMemo(),
                    paramValue != null ? String.format("%0" + (parameterMeta.getSizeInBits() / 4) + "X", paramValue) : "<value unknown>"
            );
        }

        return line;
    }
}
