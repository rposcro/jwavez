package com.rposcro.jwavez.tools.cli.controller;

import java.util.Collection;
import lombok.Builder;

@Builder
public class CommandUsageTool {

  private CommandTree commandTree;
  private String applicationCommandName;

  public StringBuffer buildGlobalUsage() {
    StringBuffer usageMessage = new StringBuffer()
        .append("usage: ").append(applicationCommandName).append(" <command> [<options>]\n")
        .append("\nAvailable commands:\n");

    commandTree.rootCommandNodes().stream()
        .forEach(node -> usageMessage.append(String.format("  %-12.12s  %s\n", node.getAlias(), node.getDescription())));
    commandTree.rootCommandNodes().stream().forEach(node -> appendComposedNodeUsage(usageMessage, node));

    return usageMessage;
  }

  public void appendComposedNodeUsage(StringBuffer buffer, CommandTreeNode commandTreeNode) {
    if (!commandTreeNode.children().isEmpty()) {
      buffer.append("\nExpanded available " + commandTreeNode.getAlias() + " commands:\n");
      appendComposedNodeUsageLines(buffer, commandTreeNode.getAlias(), commandTreeNode.children());
      buffer.append("\n");
    }
  }

  private void appendComposedNodeUsageLines(StringBuffer buffer, String parentPath, Collection<CommandTreeNode> nodes) {
    nodes.stream().forEach(node -> {
      String path = combinePath(parentPath, node.getAlias());
      node.getCommandReference().ifPresent(reference -> {
        buffer.append(String.format("  %s %s [<options>]\n", applicationCommandName, path))
            .append("    ").append(node.getDescription()).append("\n\n");
      });
      if (!node.children().isEmpty()) {
        appendComposedNodeUsageLines(buffer, path, node.children());
      }
    });
  }

  private String combinePath(String parentPath, String tail) {
    return parentPath + " " + tail;
  }
}
