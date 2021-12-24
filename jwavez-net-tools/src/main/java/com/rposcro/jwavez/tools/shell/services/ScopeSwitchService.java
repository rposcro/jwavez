package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.scopes.ScopeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Shell;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScopeSwitchService {

    @Autowired
    @Lazy
    private Shell shell;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private Map<ShellScope, ScopeContext> scopeContextMap;

    public void switchScope(ShellScope requiredScope) {
        ScopeContext currentScope = shellContext.getScopeContext();

        try {
            shellContext.setScopeContext(scopeContextMap.get(requiredScope));
            shell.gatherMethodTargets();
        } catch(Exception e) {
            shellContext.setScopeContext(currentScope);
            throw new RuntimeException("Failed to switch to expected scope!", e);
        }
    }
}
