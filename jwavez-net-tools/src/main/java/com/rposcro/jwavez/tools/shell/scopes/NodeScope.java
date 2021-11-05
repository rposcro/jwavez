package com.rposcro.jwavez.tools.shell.scopes;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class NodeScope extends WorkingScope {

    @Getter
    @Setter
    private int currentNodeId;

    @Autowired
    public NodeScope(TopScope topScope) {
        super("node", topScope);
    }
}
