package com.rposcro.jwavez.tools.shell.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static com.rposcro.jwavez.tools.shell.scopes.ShellScope.TOP;

@Component
@Scope(SCOPE_SINGLETON)
public class TopScopeContext extends ScopeContext {

    public TopScopeContext() {
        super(TOP);
    }
}
