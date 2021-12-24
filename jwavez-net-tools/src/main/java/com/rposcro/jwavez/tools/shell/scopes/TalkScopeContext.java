package com.rposcro.jwavez.tools.shell.scopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.rposcro.jwavez.tools.shell.scopes.ShellScope.TALK;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class TalkScopeContext extends ScopeContext {

    @Autowired
    public TalkScopeContext(TopScopeContext topScope) {
        super(TALK);
    }
}
