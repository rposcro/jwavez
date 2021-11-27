package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeContextCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @ShellMethod(value = "Select known node", key = { "select", "sel" })
    public String selectCurrentNodeId(@ShellOption(value = { "--node-id", "-id" }) int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation == null) {
            return String.format("Node %s is unknown, try to fetch it first", nodeId);
        } else {
            nodeScopeContext.setCurrentNodeId(nodeId);
            return "Node selection changed\n" + nodeInformationFormatter.formatShortNodeInfo(nodeInformation);
        }
    }
}
