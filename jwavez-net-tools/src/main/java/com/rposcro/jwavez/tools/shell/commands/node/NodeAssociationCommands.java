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
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
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

    @Command(name = "association print", alias = "ap", description = "Print association group(s)",
        availabilityProvider = "associationPrintCommandAvailability")
    public String printAssociationGroupDetails(
            @Option(shortName = 'g', longName = "gropup-ids") String groupIdRange,
            @Option(longName = "verbose", defaultValue = "false") boolean verbose
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

    @Command(name = "association learn", alias = "al", description = "Learn about group associations",
        availabilityProvider = "associationCommandsAvailability")
    public String fetchGroupAssociations(
            @Option(shortName = 'g', longName = "group-ids") String groupIdsRange
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

    @Command(name = "association add", alias = "aa", description = "Add association to given group",
        availabilityProvider = "associationCommandsAvailability")
    public String addAssociation(
            @Option(shortName = 'g', longName = "group-id", required = true) int groupId,
            @Option(shortName = 'd', longName = "destination-id", required = true) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "add",
                groupId,
                destinationId,
                nodeId -> associationService.sendAddAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendAddAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
    }

    @Command(name = "association remove", alias ="ar", description = "Remove association from given group",
        availabilityProvider = "associationCommandsAvailability")
    public String removeAssociation(
            @Option(shortName = 'g', longName = "group-id", required = true) int groupId,
            @Option(shortName = 'd', longName = "destination-id", required = true) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "remove",
                groupId,
                destinationId,
                nodeId -> associationService.sendRemoveAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendRemoveAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
    }

    @Bean
    public AvailabilityProvider associationCommandsAvailability() {
        Availability availability;

        if (!nodeScopeContext.isAnyNodeSelected()) {
            availability = Availability.unavailable("No node is selected in the working context, try to select or fetch one");
        } else {
            availability = shellContext.getDongleDevicePath() != null ?
                    Availability.available() :
                    Availability.unavailable("ZWave dongle device is not specified");
        }

        return AvailabilityProvider.of(availability);
    }

    @Bean
    public AvailabilityProvider associationPrintCommandAvailability() {
        return AvailabilityProvider.of(nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one"));
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
        if (groupIdsRange != null && !groupIdsRange.trim().isEmpty() && !"*".equals(groupIdsRange)) {
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
