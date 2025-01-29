package com.rposcro.jwavez.tools.shell.commands.node;

import com.jwavez.jwavez.products.model.Product;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
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

    @Autowired
    private ProductsSpecificationsProvider productsSpecificationsProvider;

    @ShellMethod(value = "List known nodes", key = {"list", "ls"})
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
                .map(node -> {
                    Product product = productsSpecificationsProvider.findProduct(node.getNodeId());
                    return String.format("Id %3s (%02x): %s: %srepo ", node.getNodeId(), node.getNodeId(), node.getNodeMemo(), product.getProductName());
                })
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Checks node responsiveness", key = "ping")
    public String checkNodeResponsiveness(
        @ShellOption(value = {"--node-id", "-id"}, defaultValue = ShellOption.NULL) Integer nodeIdArg
    ) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        int nodeId = nodeIdArg != null ? nodeIdArg : nodeScopeContext.getCurrentNodeId();
        boolean pingAnswer = nodeInformationService.pingNode(nodeId);

        return String.format("Node %3s (%02x) is %s", nodeId, nodeId, pingAnswer ? "Alive" : "Silent");
    }

    @ShellMethod(value = "Show known node information", key = {"info", "ni"})
    public String showNodeInformation(
            @ShellOption(value = {"--node-id", "-id"}, defaultValue = ShellOption.NULL) Integer nodeIdArg,
            @ShellOption(value = {"--verbose", "-v"}, defaultValue = "false") boolean verbose
    ) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        int nodeId = nodeIdArg != null ? nodeIdArg : nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation != null) {
            return verbose ? nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation)
                    : nodeInformationFormatter.formatShortNodeInfo(nodeInformation);
        } else {
            return String.format("Node %s (%02x) is unknown, try to fetch it first", nodeId, nodeId);
        }
    }

    @ShellMethod(value = "Remove node from known list", key = {"remove"})
    public String removeNodeInformation(@ShellOption({"--node-id", "-id"}) int nodeId) {
        NodeInformation nodeInformation = nodeInformationCache.removeNodeInformation(nodeId);
        if (nodeScopeContext.isAnyNodeSelected() && nodeScopeContext.getCurrentNodeId() == nodeId) {
            nodeScopeContext.setCurrentNodeId(null);
        }

        if (nodeInformation != null) {
            return String.format("Node %s (%02x) {%s} removed from cache", nodeId, nodeId, nodeInformation.getNodeMemo());
        } else {
            return "Node id " + nodeId + " was not in cache";
        }
    }

    private NodeInformation findNodeInformation(Integer nodeIdArg) {
        int nodeId = nodeIdArg == null ? nodeScopeContext.getCurrentNodeId() : nodeIdArg;
        return nodeInformationCache.getNodeDetails(nodeId);
    }
}
