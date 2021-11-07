package com.rposcro.jwavez.tools.shell;

import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.scopes.ScopeContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JWaveZShellContext {

    private String device;
    @NonNull
    private ScopeContext scopeContext;

    public ShellScope getShellScope() {
        return scopeContext.getScope();
    }
}
