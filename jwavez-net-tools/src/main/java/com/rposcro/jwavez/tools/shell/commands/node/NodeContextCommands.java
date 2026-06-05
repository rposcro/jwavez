package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
public class NodeContextCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Command(name = "select", description = "Select known node")
    public String selectCurrentNodeId(@Argument(index = 0, description = "Node id to be selected") int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation == null) {
            return String.format("Node %s is unknown, try to fetch it first", nodeId);
        } else {
            nodeScopeContext.setCurrentNodeId(nodeId);
            return "Node selection changed\n" + nodeInformationFormatter.formatShortNodeInfo(nodeInformation);
        }
    }
}
