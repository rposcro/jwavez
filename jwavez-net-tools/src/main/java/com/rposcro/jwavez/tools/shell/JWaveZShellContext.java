package com.rposcro.jwavez.tools.shell;

import com.rposcro.jwavez.tools.shell.models.DongleInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.scopes.ScopeContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@Builder
public class JWaveZShellContext {

    @NonNull
    private File workspaceDir;
    @NonNull
    private ScopeContext scopeContext;

    private String repositoryName;
    private String dongleDevicePath;
    private DongleInformation dongleInformation;

    public ShellScope getShellScope() {
        return scopeContext.getScope();
    }

    public boolean isRepositoryOpened() {
        return repositoryName != null;
    }

    public boolean isDeviceReady() {
        return dongleDevicePath != null;
    }
}
