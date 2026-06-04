package com.rposcro.jwavez.tools.shell.events;

import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@AllArgsConstructor
public class ScopeChangedEvent {

    @NonNull
    private ShellScope scopeBefore;

    @NonNull
    private ShellScope scopeAfter;
}
