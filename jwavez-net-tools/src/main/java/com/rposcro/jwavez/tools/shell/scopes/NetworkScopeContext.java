package com.rposcro.jwavez.tools.shell.scopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static com.rposcro.jwavez.tools.shell.scopes.ShellScope.NETWORK;

@Component
@Scope(SCOPE_SINGLETON)
public class NetworkScopeContext extends ScopeContext {

    @Autowired
    public NetworkScopeContext(TopScopeContext topScope) {
        super(NETWORK);
    }
}
