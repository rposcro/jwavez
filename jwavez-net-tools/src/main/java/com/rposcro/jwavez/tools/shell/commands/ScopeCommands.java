package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.ShellContext;
import com.rposcro.jwavez.tools.shell.scopes.WorkingScope;
import com.rposcro.jwavez.tools.shell.services.ScopeSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Generic")
public class ScopeCommands {

    @Autowired
    private ShellContext shellContext;

    @Autowired
    private ScopeSwitchService scopeSwitchService;

    @ShellMethod(value = "Changes current working scope", key="scope")
    public String setCurrentScope(String scopeName) {

        WorkingScope requiredScope = null;

        if (isParentScopeRequired(scopeName)) {
            requiredScope = shellContext.getWorkingScope().isTopScope() ?
                    shellContext.getWorkingScope() : shellContext.getWorkingScope().getParent();
        } else if (isTopScopeRequired(scopeName)) {
            requiredScope = shellContext.getWorkingScope().getTopScope();
        } else {
            requiredScope = findChildScope(scopeName);
        }

        if (requiredScope != null) {
            scopeSwitchService.switchScope(requiredScope);
            return "Current scope changed to " + shellContext.getWorkingScope().getScopePath();
        }

        String scopesAvailable = shellContext.getWorkingScope().getChildren().stream()
                .map(WorkingScope::getName)
                .collect(Collectors.joining(", "));
        return "Scope '" + scopeName + "' not found, available scopes are { " + scopesAvailable + " }";
    }

    private WorkingScope findChildScope(String scopeName) {
        return shellContext.getWorkingScope().getChildren().stream()
                .filter(scope -> scopeName.equals(scope.getName()))
                .findFirst()
                .orElse(null);
    }

    private boolean isParentScopeRequired(String scopeName) {
        return "..".equals(scopeName);
    }

    private boolean isTopScopeRequired(String scopeName) {
        return "/".equals(scopeName);
    }
}
