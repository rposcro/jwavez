package com.rposcro.jwavez.tools.shell.commands.talk;

import com.jwavez.jwavez.products.model.Product;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.ProductsSpecificationsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;

import java.util.stream.Collectors;

@ShellComponent
@Command(group = CommandGroup.TALK)
public class NodesRelatedCommands {

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private ProductsSpecificationsProvider productsSpecificationsProvider;

    @Command(command = "list", alias = "ls", description = "List known nodes")
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
            .map(node -> {
                Product product = productsSpecificationsProvider.findProduct(node.getNodeId());
                return String.format("Id %3s (%02x): %s: %srepo ", node.getNodeId(), node.getNodeId(), node.getNodeMemo(), product.getProductName());
            })
            .collect(Collectors.joining("\n"));
    }
}
