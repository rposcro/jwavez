package com.rposcro.jwavez.tools.shell;

import com.rposcro.jwavez.tools.shell.scopes.WorkingScope;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShellContext {

    private String device;
    private WorkingScope workingScope;
}
