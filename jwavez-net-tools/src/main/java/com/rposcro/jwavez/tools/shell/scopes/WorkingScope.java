package com.rposcro.jwavez.tools.shell.scopes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WorkingScope {

    private WorkingScope parent;
    private List<WorkingScope> children;
    private String name;

    protected WorkingScope(String name, WorkingScope parent) {
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>();

        if (parent != null) {
            parent.registerChild(this);
        }
    }

    protected WorkingScope(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public String getScopePath() {
        if (parent == null) {
            return "/";
        } else {
            return parent.getScopePath() + getName() + "/";
        }
    }

    public WorkingScope getParent() {
        return this.parent;
    }

    public List<WorkingScope> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public WorkingScope getTopScope() {
        WorkingScope candidate = this;
        while (this.parent != null) {
            candidate = candidate.parent;
        }
        return candidate;
    }

    public boolean isTopScope() {
        return this.parent == null;
    }

    protected void registerChild(WorkingScope workingScope) {
        this.children.add(workingScope);
    }
}
