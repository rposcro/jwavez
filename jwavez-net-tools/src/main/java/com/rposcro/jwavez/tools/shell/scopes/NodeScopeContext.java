package com.rposcro.jwavez.tools.shell.scopes;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static com.rposcro.jwavez.tools.shell.scopes.ShellScope.NODE;

@Component
@Scope(SCOPE_SINGLETON)
public class NodeScopeContext extends ScopeContext {

    @Getter
    @Setter
    private Integer currentNodeId;

    @Autowired
    public NodeScopeContext(TopScopeContext topScope) {
        super(NODE);
    }

    public boolean isAnyNodeSelected() {
        return currentNodeId != null;
    }

    @Override
    public String formatContext() {
        return currentNodeId != null ?
            "Selected node is: " + currentNodeId + "\n" : "No node is currently selected\n";
    }
}
