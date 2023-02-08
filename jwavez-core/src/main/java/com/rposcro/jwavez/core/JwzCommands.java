package com.rposcro.jwavez.core;

import com.rposcro.jwavez.core.commands.JwzControlledCommandFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JwzCommands {

    private JwzControlledCommandFactory controlledCommandFactory;

    public JwzCommands() {
        this.controlledCommandFactory = new JwzControlledCommandFactory();
    }

    public JwzControlledCommandFactory controlledCommandFactory() {
        return this.controlledCommandFactory;
    }
}
