package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ScopeSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.GENERIC)
public class GenericScopeCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ScopeSwitchService scopeSwitchService;

    @Command(name = "scope", description = "Show or change current working scope")
    public String manageCurrentScope(@Option(shortName = 's', longName = "--scope-name", required = true) String scopeName) {

        if (scopeName == null || scopeName.trim().isEmpty()) {
            return "Current working scope is " + shellContext.getShellScope().getScopePath();
        }

        ShellScope requiredScope;

        if (isParentScopeRequested(scopeName)) {
            requiredScope = shellContext.getShellScope() == ShellScope.TOP ?
                    shellContext.getShellScope() : shellContext.getShellScope().getParent();
        } else if (isTopScopeRequested(scopeName)) {
            requiredScope = ShellScope.TOP;
        } else {
            requiredScope = parseScopePath(scopeName);
        }

        if (requiredScope != null) {
            scopeSwitchService.switchScope(requiredScope);
            return "Current scope changed to " + shellContext.getShellScope().getScopePath();
        }

        return "Cannot switch scope to " + scopeName;
    }

    private ShellScope parseScopePath(String scopeName) {
        try {
            String[] pathNames;

            if (scopeName.startsWith("/")) {
                pathNames = scopeName.split("/");
            } else {
                pathNames = new String[]{scopeName};
            }

            ShellScope scope = ShellScope.TOP;
            for (String pathName : pathNames) {
                if (!"".equals(pathName)) {
                    scope = scope.getChildByPathName(pathName);
                    if (scope == null) {
                        return null;
                    }
                }
            }
            return scope;
        } catch (Exception e) {
        }

        return null;
    }

    private boolean isParentScopeRequested(String scopeName) {
        return "..".equals(scopeName);
    }

    private boolean isTopScopeRequested(String scopeName) {
        return "/".equals(scopeName);
    }
}
