package com.rposcro.jwavez.tools.shell.scopes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class TopScope extends WorkingScope {

    public TopScope() {
        super("TOP");
    }
}
