package com.rposcro.jwavez.core;

import com.rposcro.jwavez.core.commands.JwzControlledCommandFactory;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JwzApplicationCommands {

    private JwzControlledCommandFactory controlledCommandFactory;
    private JwzSupportedCommandParser supportedCommandParser;

    public JwzApplicationCommands() {
        this.controlledCommandFactory = new JwzControlledCommandFactory();
        this.supportedCommandParser = new JwzSupportedCommandParser(SupportedCommandResolversRegistry.instance());
    }

    public JwzControlledCommandFactory controlledCommandFactory() {
        return this.controlledCommandFactory;
    }

    public JwzSupportedCommandParser supportedCommandParser() {
        return this.supportedCommandParser;
    }
}
