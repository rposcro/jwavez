package com.rposcro.jwavez.tools.shell.commands;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ScopeSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.GENERIC)
public class GenericScopeCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ScopeSwitchService scopeSwitchService;

    @ShellMethod(value = "Show or change current working scope", key = "scope")
    public String manageCurrentScope(@ShellOption(value = {"--scope-name", "-sn"}, defaultValue = ShellOption.NULL) String scopeName) {

        if (scopeName == null) {
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
