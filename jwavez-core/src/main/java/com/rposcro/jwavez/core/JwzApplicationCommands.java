package com.rposcro.jwavez.core;

import com.rposcro.jwavez.core.commands.JwzControlledCommandFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JwzApplicationCommands {

    private JwzControlledCommandFactory controlledCommandFactory;

    public JwzApplicationCommands() {
        this.controlledCommandFactory = new JwzControlledCommandFactory();
    }

    public JwzControlledCommandFactory controlledCommandFactory() {
        return this.controlledCommandFactory;
    }
}
