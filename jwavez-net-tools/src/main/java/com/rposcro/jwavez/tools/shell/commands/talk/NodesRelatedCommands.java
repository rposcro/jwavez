package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup(CommandGroup.TALK)
public class NodesRelatedCommands {

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
}
