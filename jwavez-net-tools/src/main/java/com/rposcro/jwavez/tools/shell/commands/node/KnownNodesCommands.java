package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.products.model.Product;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

import java.util.stream.Collectors;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
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

    @Command(name = "list", alias = "ls", description = "List known nodes")
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
                .map(node -> {
                    Product product = productsSpecificationsProvider.findProduct(node.getNodeId());
                    return String.format("Id %3s (0x%02x): %s: %s",
                        node.getNodeId(),
                        node.getNodeId(),
                        node.getNodeMemo(),
                        product.getProductName());
                })
                .collect(Collectors.joining("\n"));
    }

    @Command(name = "ping", description = "Checks node responsiveness", availabilityProvider = "dongleAvailability")
    public String checkNodeResponsiveness(@Argument(index = 0, description = "Node id to be pinged") Integer nodeIdArg) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        int nodeId = nodeIdArg != null ? nodeIdArg : nodeScopeContext.getCurrentNodeId();
        boolean pingAnswer = nodeInformationService.pingNode(nodeId);

        return String.format("Node %3s (%02x) is %s", nodeId, nodeId, pingAnswer ? "Alive" : "Silent");
    }

    @Command(name = "info", alias = "ni", description = "Show known node information")
    public String showNodeInformation(
            @Argument(index = 0, description = "Node id to show info about") Integer nodeIdArg,
            @Option(shortName = 'v', longName = "verbose", defaultValue = "false") boolean verbose
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

    @Command(name = "memo", description = "Sets node memo")
    public String setNodeMemo(
            @Option(shortName = 'n', longName = "node-id") Integer nodeIdArg,
            @Option(longName = "memo", required = true) String memo
    ) {
        if (nodeIdArg == null && !nodeScopeContext.isAnyNodeSelected()) {
            return "No node selected, --node-id needs to be provided";
        }

        int nodeId = nodeIdArg != null ? nodeIdArg : nodeScopeContext.getCurrentNodeId();
        NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
        if (nodeInformation != null) {
            nodeInformation.setNodeMemo(memo);
            return String.format("Node %s (%02x) is now called %s", nodeId, nodeId, memo);
        } else {
            return String.format("Node %s (%02x) is unknown, try to fetch it first", nodeId, nodeId);
        }
    }

    @Command(name = "remove", description = "Remove node from known list")
    public String removeNodeInformation(@Argument(index = 0, description = "Node id to remove from known list") int nodeId) {
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
}
