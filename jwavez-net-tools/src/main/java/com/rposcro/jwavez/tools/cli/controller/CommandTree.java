package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.Builder;

public class CommandTree {

    private CommandTreeNode rootNode;

    @Builder
    public CommandTree(CommandTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public CommandLineContent scanCommandLine(String... args) throws CommandLineException {
        if (args.length < 1) {
            throw new CommandLineException("Wrong program arguments");
        }

        List<String> expectedCommmandPath = extractCommandPath(args);
        CommandTreeNode commandNode = findClosestMatchingCommandNode(expectedCommmandPath);

        return CommandLineContent.builder()
                .commandNode(commandNode)
                .commandOtions(Arrays.copyOfRange(args, commandNode.path().size(), args.length))
                .build();
    }

    public Collection<CommandTreeNode> rootCommandNodes() {
        return rootNode.getChildren();
    }

    private List<String> extractCommandPath(String[] args) {
        List<String> path = new ArrayList<>();
        for (String arg : args) {
            if (!isAlias(arg)) {
                break;
            }
            path.add(arg);
        }
        return path;
    }

    private CommandTreeNode findClosestMatchingCommandNode(List<String> commandPath) throws CommandLineException {
        CommandTreeNode treeNode = rootNode;
        for (String alias : commandPath) {
            if (!treeNode.hasChild(alias)) {
                break;
            }
            treeNode = treeNode.findChild(alias);
        }

        if (treeNode.isRoot()) {
            throw new CommandLineException(String.format("Unrecognized command: '%s'", String.join(" ", commandPath)));
        }

        return treeNode;
    }

    private boolean isAlias(String arg) {
        return !arg.startsWith("-");
    }
}
