package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class NodeContextCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private JWaveZShellContext shellContext;

    @ShellMethod(value = "Shows current working context information", key = "pwc")
    public String showCurrentNodeId() {
        String selNodeIdInfo = nodeScopeContext.isAnyNodeSelected() ?
                "Selected node is: " + nodeScopeContext.getCurrentNodeId() : "No node is currently selected";
        return "Current working scope is: " + shellContext.getShellScope().getScopePath() + "\n" + selNodeIdInfo;
    }

    @ShellMethod(value = "Select node", key = "select")
    public String selectCurrentNodeId(int nodeId) {
        if (nodeId <= 0) {
            return "Current node id is " + nodeScopeContext.getCurrentNodeId() + "\n";
        } else {
            nodeScopeContext.setCurrentNodeId(nodeId);
            return "Selected node changed to " + nodeId + "\n";
        }
    }

    @ShellMethod(value = "Lists known nodes", key = { "list", "ls" })
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
                .map(node -> "Id " + node.getNodeId() + ": " + node.getNodeMemo())
                .collect(Collectors.joining("\n"));
    }
}
