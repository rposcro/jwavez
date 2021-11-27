package com.rposcro.jwavez.tools.shell.scopes;

public abstract class ScopeContext {

    private final ShellScope shellScope;

    protected ScopeContext(ShellScope shellScope) {
        this.shellScope = shellScope;
    }

    public ShellScope getScope() {
        return this.shellScope;
    }

    public String formatContext() {
        return "";
    }
}
