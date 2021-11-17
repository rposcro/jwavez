package com.rposcro.jwavez.tools.shell.scopes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum ShellScope {

    TOP(null),
    DONGLE(TOP),
    NETWORK(TOP),
    NODE(TOP);

    @Getter
    private ShellScope parent;
    @Getter
    private List<ShellScope> children;

    private String pathName;

    ShellScope(ShellScope parent) {
        this.parent = parent;
        this.children = new ArrayList<>(5);
        this.pathName = this.name().toLowerCase();

        if (parent != null) {
            parent.registerChild(this);
        }
    }

    private void registerChild(ShellScope child) {
        this.children.add(child);
    }

    public String getScopePath() {
        return getScopePath("/");
    }

    public String getScopePath(String separator) {
        if (parent == null) {
            return separator;
        } else {
            return parent.getScopePath(separator) + pathName + separator;
        }
    }

    public ShellScope getChildByPathName(String pathName) {
        return children.stream()
                .filter(scope -> scope.pathName.equals(pathName))
                .findFirst()
                .orElse(null);
    }

    public boolean hasChild(ShellScope child) {
        return children.stream().anyMatch(scope -> scope == child);
    }
}
