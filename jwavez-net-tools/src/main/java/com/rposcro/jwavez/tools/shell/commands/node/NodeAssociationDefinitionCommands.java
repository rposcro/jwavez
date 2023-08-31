package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeAssociationDefinitionService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeAssociationDefinitionCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeAssociationDefinitionService definitionService;

    @ShellMethod(value = "Define node association group", key = {"association define"})
    public String defineAssociationGroup(
            @ShellOption(value = {"--group-id", "-gi"}) int associationGroupId,
            @ShellOption(value = {"--group-memo", "-memo"}) String associationGroupMemo
    ) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        definitionService.updateOrCreateMeta(nodeId, associationGroupId, associationGroupMemo);
        return String.format("Association group defined:\n  node id: %s\n  group id: %s\n  memo: %s"
                , nodeId, associationGroupId, associationGroupMemo);
    }

    @ShellMethod(value = "Clone node association groups definitions from given node to current", key = {"association clone"})
    public String cloneAssociationGroups(
            @ShellOption(value = {"--node-id", "-id"}) int sourceNodeId
    ) {
        int currentNodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation currentNode = nodeInformationCache.getNodeDetails(currentNodeId);
        NodeInformation sourceNode = nodeInformationCache.getNodeDetails(sourceNodeId);

        if (sourceNode != null) {
            if (nodeInformationService.nodesMatch(currentNode, sourceNode)) {
                definitionService.cloneGroupMetas(sourceNodeId, currentNodeId);
                return "Association groups definitions cloned to current node";
            } else {
                return "Node " + sourceNodeId + " doesn't match the current one, cannot clone from it";
            }
        } else {
            return "Node " + sourceNodeId + " is unknown, cannot clone from it";
        }
    }

    @ShellMethod(value = "Delete node association group definition", key = {"association delete"})
    public String deleteAssociationGroupDefinitionAndValues(
            @ShellOption(value = {"--group-id", "-gi"}) int associationGroupId
    ) {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (definitionService.removeGroup(nodeId, associationGroupId) != null) {
            return "Association group " + associationGroupId + " deleted";
        } else {
            return "Unknown association group " + associationGroupId;
        }
    }

    @ShellMethodAvailability
    public Availability checkLocalAvailability() {
        return nodeScopeContext.isAnyNodeSelected() ?
                Availability.available() :
                Availability.unavailable("No node is selected in the working context, try to select or fetch one");
    }
}
