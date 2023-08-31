package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.AssociationGroupMeta;
import com.rposcro.jwavez.tools.shell.models.CommandClassMeta;
import com.rposcro.jwavez.tools.shell.models.EndPointMark;
import com.rposcro.jwavez.tools.shell.models.NodeAddress;
import com.rposcro.jwavez.tools.shell.models.NodeAssociationsInformation;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NodeAssociationService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeMultiChannelAssociationService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeAssociationCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ConsoleAccessor console;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeAssociationService associationService;

    @Autowired
    private NodeMultiChannelAssociationService multiChannelAssociationService;

    @Autowired
    private NumberRangeParser numberRangeParser;

    @ShellMethod(value = "Print association group(s)", key = {"association print", "ap"})
    public String printAssociationGroupDetails(
            @ShellOption(value = {"--gropup-ids", "-gis"}, defaultValue = ShellOption.NULL) String groupIdRange,
            @ShellOption(defaultValue = "false") boolean verbose
    ) {
        try {
            int[] groupIds = parseGroupIdsArgument(groupIdRange);
            StringBuffer groupDetails = new StringBuffer();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
            for (int groupId : groupIds) {
                groupDetails.append(verbose ? formatVerboseLine(nodeInformation, groupId) : formatValueLine(nodeInformation, groupId));
                groupDetails.append('\n');
            }
            return groupDetails.toString();
        } catch (ParseException e) {
            return "Cannot parse argument: " + groupIdRange + "\n";
        }
    }

    @ShellMethod(value = "Learn about group associations", key = {"association learn", "al"})
    public String fetchGroupAssociations(
            @ShellOption(value = {"--group-ids", "-gis"}, defaultValue = ShellOption.NULL) String groupIdsRange
    ) throws SerialException {
        try {
            int[] groupIds = parseGroupIdsArgument(groupIdsRange);
            int nodeId = nodeScopeContext.getCurrentNodeId();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

            StringBuffer groupDetails = new StringBuffer();
            for (int groupId : groupIds) {
                if (supportsMultiChannel(nodeInformation)) {
                    multiChannelAssociationService.fetchMultiChannelAssociations(nodeId, groupId);
                } else {
                    associationService.fetchGroupAssociations(nodeId, groupId);
                }
                groupDetails.append(formatVerboseLine(nodeInformation, groupId));
                groupDetails.append('\n');
            }
            return groupDetails.toString();

        } catch (ParseException e) {
            return "Cannot parse argument: " + groupIdsRange + "\n";
        }
    }

    @ShellMethod(value = "Add association to given group", key = {"association add", "aa"})
    public String addAssociation(
            @ShellOption(value = {"--group-id", "-gi"}) int groupId,
            @ShellOption(value = {"--destination-id", "-di"}) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "add",
                groupId,
                destinationId,
                nodeId -> associationService.sendAddAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendAddAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
    }

    @ShellMethod(value = "Remove association from given group", key = {"association remove", "ar"})
    public String removeAssociation(
            @ShellOption(value = {"--group-id", "-gi"}) int groupId,
            @ShellOption(value = {"--destination-id", "-di"}) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "remove",
                groupId,
                destinationId,
                nodeId -> associationService.sendRemoveAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendRemoveAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
    }

    @ShellMethodAvailability(value = {"association learn", "association add", "association remove"})
    public Availability checkRemoteAvailability() {
        if (!nodeScopeContext.isAnyNodeSelected()) {
            return Availability.unavailable("No node is selected in the working context, try to select or fetch one");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    @ShellMethodAvailability({"association print"})
    public Availability checkLocalAvailability() {
        return nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one");
    }

    private NodeInformation executeAssociationAction(
            String actionName,
            int groupId,
            String destinationId,
            SerialFunction<Integer, Boolean> associationAction,
            SerialFunction<Integer, Boolean> mchAssociationAction)
            throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        boolean success;

        if (!nodeInformation.getAssociationsInformation().isGroupDefined(groupId)) {
            console.flushLine(format("Association group %02x is not known for node %02x", groupId, nodeId));
            success = false;
        }

        if (EndPointMark.isCorrectMark(destinationId)) {
            if (!supportsMultiChannel(nodeInformation)) {
                console.flushLine(format("Node %02x doesn't support multi channel associations!", nodeId));
                success = false;
            } else {
                success = mchAssociationAction.execute(nodeId);
            }
        } else if (NodeAddress.isCorrectAddress(destinationId)) {
            success = associationAction.execute(nodeId);
        } else {
            console.flushLine(format("Invalid destination address format: %s", destinationId));
            success = false;
        }

        if (success) {
            console.flushLine(format("Action %s successfully completed on group %s", actionName, groupId));
        } else {
            console.flushLine(format("Something went wrong and %s action was not completed on association group %02x",
                    actionName, groupId
            ));
        }

        return nodeInformation;
    }

    private int[] parseGroupIdsArgument(String groupIdsRange) throws ParseException {
        if (groupIdsRange != null && !"*".equals(groupIdsRange)) {
            return numberRangeParser.parseNumberRange(groupIdsRange);
        } else {
            return nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId())
                    .getAssociationsInformation().getAssociationGroupsMetas().stream()
                    .mapToInt(AssociationGroupMeta::getGroupId)
                    .toArray();
        }
    }

    private boolean supportsMultiChannel(NodeInformation nodeInformation) {
        return Stream.of(nodeInformation.getProductInformation().getCommandClasses())
                .map(CommandClassMeta::getCommandClass)
                .anyMatch(cmdClass -> CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION == cmdClass);
    }

    private String formatValueLine(NodeInformation nodeInformation, int groupId) {
        AssociationGroupMeta groupMeta = nodeInformation.getAssociationsInformation().findGroupMeta(groupId);
        NodeAssociationsInformation associations = nodeInformation.getAssociationsInformation();
        String line;

        if (groupMeta == null) {
            line = format("Association group %02x: <group unknown>", groupId);
        } else {
            line = format("Association group %02x: [ %s ], [ %s ]",
                    groupId,
                    formatNodesList(associations.findNodeAssociations(groupId)),
                    formatEndPointsList(associations.findEndPointAssociations(groupId)));
        }

        return line;
    }

    private String formatVerboseLine(NodeInformation nodeInformation, int groupId) {
        AssociationGroupMeta groupMeta = nodeInformation.getAssociationsInformation().findGroupMeta(groupId);
        NodeAssociationsInformation associations = nodeInformation.getAssociationsInformation();
        String line;

        if (groupMeta == null) {
            line = format("Association group %02x: <group unknown>", groupId);
        } else {
            line = format("Association group %02x:\n  memo: %s\n  nodes: [ %s ]\n  endPoints: [ %s ]",
                    groupMeta.getGroupId(),
                    groupMeta.getMemo(),
                    formatNodesList(associations.findNodeAssociations(groupId)),
                    formatEndPointsList(associations.findEndPointAssociations(groupId)));
        }

        return line;
    }

    private String formatNodesList(List<Integer> nodes) {
        return nodes.stream().map(id -> format("%02x", id)).collect(Collectors.joining(", "));
    }

    private String formatEndPointsList(List<EndPointMark> nodes) {
        return nodes.stream().map(EndPointMark::getMark).collect(Collectors.joining(", "));
    }
}
