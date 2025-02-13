package com.rposcro.jwavez.tools.shell.commands.node;

import com.jwavez.jwavez.products.model.AssociationGroup;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
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
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
import com.rposcro.jwavez.tools.utils.SerialFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@ShellComponent
@Command(group = CommandGroup.NODE)
public class NodeAssociationCommands {

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

    @Autowired
    private ProductsSpecificationsProvider productsSpecificationsProvider;

    @Command(command = "association print", alias = "ap", description = "Print association group(s)")
    @CommandAvailability(provider = {"nodeAvailability"})
    public String printAssociationGroupDetails(
            @Option(longNames = "group-ids", shortNames = 'g', defaultValue = "*") String groupIdRange,
            @Option(longNames = "verbose", shortNames = 'v', defaultValue = "false") boolean verbose
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

    @Command(command = "association learn", alias = "al", description = "Learn about group associations")
    @CommandAvailability(provider = {"dongleAvailability", "nodeAvailability"})
    public String fetchGroupAssociations(
            @Option(longNames = "group-ids", shortNames = 'g', required = true) String groupIdsRange,
            @Option(longNames = "multichannel", shortNames = 'm', defaultValue = "true") boolean useMultiChannel
    ) throws SerialException {
        try {
            int[] groupIds = parseGroupIdsArgument(groupIdsRange);
            int nodeId = nodeScopeContext.getCurrentNodeId();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

            StringBuffer groupDetails = new StringBuffer();
            for (int groupId : groupIds) {
                if (useMultiChannel && supportsMultiChannel(nodeInformation)) {
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

    @Command(command = "association add", alias = "aa", description = "Add association to given group")
    @CommandAvailability(provider = {"dongleAvailability", "nodeAvailability"})
    public String addAssociation(
            @Option(longNames = "group-id", shortNames = 'g', required = true) int groupId,
            @Option(longNames = "destination-id", shortNames = 'd', required = true) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "add",
                groupId,
                destinationId,
                nodeId -> associationService.sendAddAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendAddAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
    }

    @Command(command = "association remove", alias = "ar", description = "Remove association from given group")
    @CommandAvailability(provider = {"dongleAvailability", "nodeAvailability"})
    public String removeAssociation(
            @Option(longNames = "group-id", shortNames = 'g', required = true) int groupId,
            @Option(longNames = "destination-id", shortNames = 'd', required = true) String destinationId
    ) throws SerialException {
        NodeInformation nodeInformation = executeAssociationAction(
                "remove",
                groupId,
                destinationId,
                nodeId -> associationService.sendRemoveAssociation(nodeId, groupId, Integer.parseInt(destinationId)),
                nodeId -> multiChannelAssociationService.sendRemoveAssociation(nodeId, groupId, new EndPointMark(destinationId)));
        return formatValueLine(nodeInformation, groupId) + "\n";
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

        if (!productsSpecificationsProvider.hasAssociationGroup(nodeId, groupId)) {
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
            return productsSpecificationsProvider.findAssociationsGroupsIds(nodeScopeContext.getCurrentNodeId());
        }
    }

    private boolean supportsMultiChannel(NodeInformation nodeInformation) {
        return Stream.of(nodeInformation.getProductInformation().getCommandClasses())
                .map(CommandClassMeta::getCommandClass)
                .anyMatch(cmdClass -> CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION == cmdClass);
    }

    private String formatValueLine(NodeInformation nodeInformation, int groupId) {
        NodeAssociationsInformation associations = nodeInformation.getAssociationsInformation();
        String line;

        if (!productsSpecificationsProvider.hasAssociationGroup(nodeInformation.getNodeId(), groupId)) {
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
        AssociationGroup associationGroup = productsSpecificationsProvider.findProduct(nodeInformation).findAssociationGroup(groupId);
        NodeAssociationsInformation associations = nodeInformation.getAssociationsInformation();
        String line;

        if (associationGroup == null) {
            line = format("Association group %02x: <group unknown>", groupId);
        } else {
            line = format("Association group %02x:\n  memo: %s\n  nodes: [ %s ]\n  endPoints: [ %s ]",
                associationGroup.getGroupId(),
                associationGroup.getDescription(),
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
