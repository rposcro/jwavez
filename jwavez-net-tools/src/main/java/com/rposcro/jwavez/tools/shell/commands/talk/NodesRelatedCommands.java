package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;

import java.util.stream.Collectors;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.TALK)
public class NodesRelatedCommands {

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Command(name = "list", alias = "ls", description = "List known nodes")
    public String listKnownNodes() {
        return nodeInformationCache.getOrderedNodeList().stream()
                .map(node -> "Id " + node.getNodeId() + ": " + node.getNodeMemo())
                .collect(Collectors.joining("\n"));
    }
}
