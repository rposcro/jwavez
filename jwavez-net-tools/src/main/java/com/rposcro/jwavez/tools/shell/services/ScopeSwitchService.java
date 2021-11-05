package com.rposcro.jwavez.tools.shell.services;

import com.rposcro.jwavez.tools.shell.ShellContext;
import com.rposcro.jwavez.tools.shell.scopes.WorkingScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.stereotype.Service;

@Service
public class ScopeSwitchService {

    @Autowired
    private Shell shell;

    @Autowired
    private ShellContext shellContext;

    public void switchScope(WorkingScope requiredScope) {
        WorkingScope currentScope = shellContext.getWorkingScope();

        try {
            shellContext.setWorkingScope(requiredScope);
            shell.gatherMethodTargets();
        } catch(Exception e) {
            shellContext.setWorkingScope(currentScope);
            throw new RuntimeException("Failed to switch to expected scope!", e);
        }
    }
}
