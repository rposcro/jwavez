package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.Command;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.cli.Options;

@Getter
public class CommandTreeNode {

  private String alias;
  private String description;
  private Optional<CommandReference> commandReference;
  private Optional<CommandTreeNode> parent;

  private HashMap<String, CommandTreeNode> children;

  public CommandTreeNode(String alias, String description) {
    this.alias = alias;
    this.description = description;
    this.children = new LinkedHashMap<>();
    this.commandReference = Optional.empty();
    this.parent = Optional.empty();
  }

  public Collection<CommandTreeNode> children() {
    return children.values();
  }

  public CommandTreeNode withCommandReference(CommandReference commandReference) {
    this.commandReference = Optional.of(commandReference);
    return this;
  }

  public CommandTreeNode withCommandReference(Class<? extends Command> commandClass, Options options) {
    this.commandReference = Optional.of(new CommandReference(commandClass, options));
    return this;
  }

  public CommandTreeNode withChild(CommandTreeNode childNode) {
    children.put(childNode.getAlias(), childNode);
    childNode.parent = Optional.of(this);
    return this;
  }

  public CommandTreeNode findChild(String alias) {
    return children.get(alias);
  }

  public List<String> path() {
    List<String> path = new ArrayList<>();
    attachPath(path);
    return path;
  }

  private void attachPath(List<String> path) {
    path.add(0, alias);
    parent.ifPresent(node -> node.attachPath(path));
  }
}
