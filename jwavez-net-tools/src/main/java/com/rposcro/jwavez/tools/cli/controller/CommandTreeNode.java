package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import org.apache.commons.cli.Options;

@Getter
public class CommandTreeNode {

    private String alias;
    private String description;
    private Optional<CommandReference> commandReference;
    private Optional<CommandTreeNode> parent;

    private HashMap<String, CommandTreeNode> subCommandTree;

    public CommandTreeNode(String alias, String description) {
        this.alias = alias;
        this.description = description;
        this.subCommandTree = new LinkedHashMap<>();
        this.commandReference = Optional.empty();
        this.parent = Optional.empty();
    }

    public Collection<CommandTreeNode> getChildren() {
        return subCommandTree.values();
    }

    public CommandTreeNode withCommandReference(Class<? extends Command> commandClass, Options options) {
        this.commandReference = Optional.of(new CommandReference(commandClass, options));
        return this;
    }

    public CommandTreeNode addChild(CommandTreeNode childNode) {
        subCommandTree.put(childNode.getAlias(), childNode);
        childNode.parent = Optional.of(this);
        return this;
    }

    public boolean isRoot() {
        return !parent.isPresent();
    }

    public CommandTreeNode findChild(String alias) {
        return subCommandTree.get(alias);
    }

    public boolean hasChild(String alias) {
        return subCommandTree.containsKey(alias);
    }

    public String pathAsString() {
        return path().stream().collect(Collectors.joining(" "));
    }

    public List<String> path() {
        List<String> path = new ArrayList<>();
        attachPath(path);
        return path;
    }

    private void attachPath(List<String> path) {
        parent.ifPresent(parentNode -> {
            path.add(0, alias);
            parentNode.attachPath(path);
        });
    }
}
