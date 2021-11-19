package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
public class KnownNodesCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @ShellMethod(value = "List known nodes", key = { "list", "ls" })
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
                .map(node -> "Id " + node.getNodeId() + ": " + node.getNodeMemo())
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Show known node information", key = { "info", "ni" })
    public String showNodeInformation(
            @ShellOption(value = { "--node-id", "-id" }, defaultValue = ShellOption.NULL) Integer nodeIdArg,
            @ShellOption(value = { "--verbose", "-v" }, defaultValue = "false") boolean verbose
    ) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeIdArg);
        if (nodeInformation != null) {
            return verbose ? nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation)
                    : nodeInformationFormatter.formatShortNodeInfo(nodeInformation);
        } else {
            return String.format("Node %s is unknown, try to fetch it first", nodeIdArg);
        }
    }

    @ShellMethod(value = "Set known node memo", key = { "memo" })
    public String setNodeMemo(
            @ShellOption(value = { "--node-memo", "-memo" }) String nodeMemo,
            @ShellOption(value = { "--node-id", "-id" }, defaultValue = ShellOption.NULL) Integer nodeIdArg
            ) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        NodeInformation nodeInformation = findNodeInformation(nodeIdArg);
        if (nodeInformation != null) {
            nodeInformation.setNodeMemo(nodeMemo);
            nodeInformationCache.persist();
            return "Memo changed\n" + nodeInformationFormatter.formatShortNodeInfo(nodeInformation);
        } else {
            return String.format("Node %s is unknown, try to fetch it first", nodeIdArg);
        }
    }

    @ShellMethod(value = "Remove node from known list", key = { "remove" })
    public String removeNodeInformation(@ShellOption({ "--node-id", "-id" }) int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.removeNodeDetails(nodeId);
        if (nodeScopeContext.isAnyNodeSelected() && nodeScopeContext.getCurrentNodeId() == nodeId) {
            nodeScopeContext.setCurrentNodeId(null);
        }

        if (nodeInformation != null) {
            nodeInformationCache.persist();
            return String.format("Node %s (%s) removed from cache", nodeId, nodeInformation.getNodeMemo());
        } else {
            return "Node id " + nodeId + " was not in cache";
        }
    }

    @ShellMethod(value = "Match current node with others in cache", key = { "match" })
    public String matchNodeInCache(@ShellOption({ "--node-id", "-id" }) int nodeId) {
        int currentNodeId = nodeScopeContext.getCurrentNodeId();
        NodeInformation currentNode = nodeInformationCache.getNodeDetails(nodeId);
        List<NodeInformation> discoveredNodes = nodeInformationService.findMatchingNodes(nodeId);
        if (discoveredNodes.isEmpty()) {
            return String.format("No similar devices discovered for current node %s (%s)", currentNodeId, currentNode.getNodeMemo());
        } else {
            StringBuffer message = new StringBuffer(String.format("Discovered nodes similar to current %s (%s)",
                    currentNodeId, currentNode.getNodeMemo()));
            discoveredNodes.stream().forEach(
                    node -> message.append(String.format("Id: %s (%s) with %s parameters",
                            node.getNodeId(), node.getNodeMemo(), node.getParametersInformation().getParameterMetas().size()))
            );
            return message.toString();
        }
    }

    private NodeInformation findNodeInformation(Integer nodeIdArg) {
        int nodeId = nodeIdArg == null ? nodeScopeContext.getCurrentNodeId() : nodeIdArg;
        return nodeInformationCache.getNodeDetails(nodeId);
    }
}
