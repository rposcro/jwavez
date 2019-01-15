package com.rposcro.jwavez.tools.cli.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import org.apache.commons.cli.HelpFormatter;

public class CommandHelpTool {

  private CommandTree commandTree;

  @Builder
  public CommandHelpTool(CommandTree commandTree) {
    this.commandTree = commandTree;
  }

  public StringBuffer buildCommandHelp(CommandTreeNode node) {
    List<String> path = node.path();
    StringBuffer helpMessage = new StringBuffer()
        .append("Help on command: " + combinePath(node) + "\n")
        ;
    appendNodeHelp(helpMessage, node);
    return helpMessage;
  }

  private void appendNodeHelp(StringBuffer buffer, CommandTreeNode node) {
    node.getCommandReference().ifPresent(reference -> {
      buffer.append("\nOptions for command: ").append(combinePath(node)).append("\n")
          .append(commandOptions(reference))
          ;
    });
    node.children().stream().forEachOrdered(child -> appendNodeHelp(buffer, child));
  }

  private StringBuffer commandOptions(CommandReference reference) {
    StringWriter strWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(strWriter);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printOptions(writer, 80, reference.getOptionsDefinition(), 2, 1);
    return strWriter.getBuffer();
  }

  private String combinePath(CommandTreeNode node) {
    return node.path().stream().collect(Collectors.joining(" "));
  }
}
