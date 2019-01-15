package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandTree {

  private Map<String, CommandTreeNode> commandTree;

  public CommandTree() {
    this.commandTree = new LinkedHashMap<>();
  }

  public CommandTree addNode(CommandTreeNode treeNode) {
    commandTree.put(treeNode.getAlias(), treeNode);
    return this;
  }

  public CommandLineContent scanCommandLine(String... args) throws CommandLineException {
    if (args.length < 1) {
      throw new CommandLineException("Wrong program arguments");
    }

    List<String> commmandPath = extractCommandPath(args);
    CommandTreeNode commandNode = findCommandNode(commmandPath);

    return CommandLineContent.builder()
        .commandNode(commandNode)
        .commandOtions(Arrays.copyOfRange(args, commmandPath.size(), args.length))
        .build();
  }

  public Collection<CommandTreeNode> rootCommandNodes() {
    return commandTree.values();
  }

  private List<String> extractCommandPath(String[] args) {
    return Stream.of(args)
        .sequential()
        .filter(this::isNodeAlias)
        .collect(Collectors.toList());
  }

  private CommandTreeNode findCommandNode(List<String> commandPath) throws CommandLineException {
    if (commandPath.isEmpty()) {
      throw new CommandLineException("No command specified");
    }

    CommandTreeNode treeNode = commandTree.get(commandPath.get(0));
    int pathIndex = 1;

    while (treeNode != null && pathIndex < commandPath.size()) {
      treeNode = treeNode.findChild(commandPath.get(pathIndex));
      pathIndex++;
    }

    if (treeNode == null) {
      throw new CommandLineException("Wrong command path: " + commandPath.stream().collect(Collectors.joining()));
    }

    return treeNode;
  }

  private boolean isNodeAlias(String arg) {
    return !arg.startsWith("-");
  }
}
