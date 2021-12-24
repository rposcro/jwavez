package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.AssociationGroupMeta;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeAssociationService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import com.rposcro.jwavez.tools.shell.services.NumberRangeParser;
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

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeAssociationCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeAssociationService nodeAssociationService;

    @Autowired
    private NumberRangeParser numberRangeParser;

    @ShellMethod(value = "Define node association group", key = { "association define", "ad" })
    public String defineAssociationGroup(
            @ShellOption(value = { "--group-id", "-gi" }) int associationGroupId,
            @ShellOption(value = { "--group-memo", "-memo" }) String associationGroupMemo
    ) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        nodeAssociationService.updateOrCreateMeta(nodeId, associationGroupId, associationGroupMemo);
        return String.format("Association group defined:\n  node id: %s\n  group id: %s\n  memo: %s"
                , nodeId, associationGroupId, associationGroupMemo);
    }

    @ShellMethod(value = "Clone node association groups definitions from given node to current", key = { "association clone" })
    public String cloneAssociationGroups(
            @ShellOption(value = { "--node-id", "-id" }) int sourceNodeId
    ) {
        int currentNodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation currentNode = nodeInformationCache.getNodeDetails(currentNodeId);
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);

        if (sourceNode != null) {
            if (nodeInformationService.nodesMatch(currentNode, sourceNode)) {
                nodeAssociationService.cloneGroupMetas(sourceNodeId, currentNodeId);
                return "Association groups definitions cloned to current node";
            } else {
                return "Node " + sourceNodeId + " doesn't match the current one, cannot clone from it";
            }
        } else {
            return "Node " + sourceNodeId + " is unknown, cannot clone from it";
        }
    }

    @ShellMethod(value = "Delete node association group definition", key = { "association delete" })
    public String deleteAssociationGroupDefinitionAndValues(
            @ShellOption(value = { "--group-id", "-gi" }) int associationGroupId
    ) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeAssociationService.removeGroup(nodeId, associationGroupId) != null) {
            return "Association group " + associationGroupId + " deleted";
        } else {
            return "Unknown association group " + associationGroupId;
        }
    }

    @ShellMethod(value = "Print association group(s)", key = { "association print", "ap" })
    public String printAssociationGroupDetails(
            @ShellOption(value = { "--gropup-ids", "-gis" }, defaultValue = ShellOption.NULL) String groupIdRange,
            @ShellOption(defaultValue = "false") boolean verbose
    ) {
        try {
            int[] groupIds = parseGroupIdsArgument(groupIdRange);
            StringBuffer groupDetails = new StringBuffer();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeScopeContext.getCurrentNodeId());
            for (int groupId: groupIds) {
                groupDetails.append(verbose ? formatVerboseLine(nodeInformation, groupId) :  formatValueLine(nodeInformation, groupId));
                groupDetails.append('\n');
            }
            return groupDetails.toString();
        } catch(ParseException e) {
            return "Cannot parse argument: " + groupIdRange;
        }
    }

    @ShellMethod(value = "Learn about group associations", key = { "association learn", "al" })
    public String fetchGroupAssociations(
            @ShellOption(value = { "--group-ids", "-gis" }, defaultValue = ShellOption.NULL) String groupIdsRange
    ) throws SerialException {
        try {
            int[] groupIds = parseGroupIdsArgument(groupIdsRange);
            int nodeId = nodeScopeContext.getCurrentNodeId();
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

            StringBuffer groupDetails = new StringBuffer();
            for (int groupId: groupIds) {
                nodeAssociationService.fetchGroupAssociations(nodeId, groupId);
                groupDetails.append(formatVerboseLine(nodeInformation, groupId));
                groupDetails.append('\n');
            }
            return groupDetails.toString();

        } catch(ParseException e) {
            return "Cannot parse argument: " + groupIdsRange;
        }
    }

    @ShellMethod(value = "Add association to given group", key = { "association add", "aa" })
    public String addAssociation(
            @ShellOption(value = { "--group-id", "-gi" }) int groupId,
            @ShellOption(value = { "--assoc-node-id", "-ani" }) int nodeIdToAssociate
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

        if (!nodeInformation.getAssociationsInformation().isGroupDefined(groupId)) {
            return "Association group " + groupId + " is not known for node " + nodeId;
        }

        boolean success = nodeAssociationService.sendAddAssociation(nodeId, groupId, nodeIdToAssociate);
        if (success) {
            return String.format("Node %02X added to association group %02X", nodeIdToAssociate, groupId);
        } else {
            return "Something went wrong and node has not been added to association group";
        }
    }

    @ShellMethod(value = "Remove association from given group", key = { "association remove", "ar" })
    public String removeAssociation(
            @ShellOption(value = { "--group-id", "-gi" }) int groupId,
            @ShellOption(value = { "--assoc-node-id", "-ani" }) int nodeIdToRemove
    ) throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);

        if (!nodeInformation.getAssociationsInformation().isGroupDefined(groupId)) {
            return "Association group " + groupId + " is not known for node " + nodeId;
        }

        boolean success = nodeAssociationService.sendRemoveAssociation(nodeId, groupId, nodeIdToRemove);
        if (success) {
            return String.format("Node %02X removed from association group %02X", nodeIdToRemove, groupId);
        } else {
            return "Something went wrong and node has not been removed from association group";
        }
    }

    @ShellMethodAvailability(value = { "association learn", "association add", "association remove" })
    public Availability checkRemoteAvailability() {
        if (!nodeScopeContext.isAnyNodeSelected()) {
            return Availability.unavailable("No node is selected in the working context, try to select or fetch one");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

    @ShellMethodAvailability({ "association clone", "association define", "association delete", "association print" })
    public Availability checkLocalAvailability() {
        return nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one");
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

    private String formatValueLine(NodeInformation nodeInformation, int groupId) {
        AssociationGroupMeta groupMeta = nodeInformation.getAssociationsInformation().findGroupMeta(groupId);
        List<Integer> associations = nodeInformation.getAssociationsInformation().findAssociations(groupId);
        String line;

        if (groupMeta == null) {
            line = String.format("Association group %02X: <group unknown>", groupId);
        } else if (associations == null) {
            line = String.format("Association group %02X: <nodes unknown>", groupId);
        } else {
            line = String.format("Association group %02X: %s",
                    groupId,
                    associations != null ?
                            associations.stream().map(nodeId -> String.format("%02X", nodeId)).collect(Collectors.joining(", "))
                            : "<nodes unknown>"
            );
        }
        return line;
    }

    private String formatVerboseLine(NodeInformation nodeInformation, int groupId) {
        AssociationGroupMeta groupMeta = nodeInformation.getAssociationsInformation().findGroupMeta(groupId);
        List<Integer> associations = nodeInformation.getAssociationsInformation().findAssociations(groupId);
        String line;

        if (groupMeta == null) {
            line = "Association group " + groupId + ": <group unknown>";
        } else {
            line = String.format("Association group %s:\n  memo: %s\n  nodes: %s",
                    groupMeta.getGroupId(),
                    groupMeta.getMemo(),
                    associations != null ?
                            associations.stream().map(nodeId -> String.format("%02X", nodeId)).collect(Collectors.joining(", "))
                            : "<nodes unknown>"
            );
        }

        return line;
    }
}
