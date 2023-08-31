package com.rposcro.jwavez.core;

import com.rposcro.jwavez.core.commands.JwzControlledCommandFactory;
import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.listeners.SupportedCommandDispatcher;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class JwzApplicationSupport {

    private static JwzApplicationSupport DEFAULT_INSTANCE;

    private JwzControlledCommandFactory controlledCommandFactory;
    private JwzSupportedCommandParser supportedCommandParser;
    private SupportedCommandDispatcher supportedCommandDispatcher;

    private JwzApplicationSupport() {
        this.controlledCommandFactory = new JwzControlledCommandFactory();
        this.supportedCommandParser = new JwzSupportedCommandParser(SupportedCommandResolversRegistry.instance());
        this.supportedCommandDispatcher = new SupportedCommandDispatcher();
    }

    public static JwzApplicationSupport defaultSupport() {
        return DEFAULT_INSTANCE == null ? DEFAULT_INSTANCE = new JwzApplicationSupport() : DEFAULT_INSTANCE;
    }

    public JwzControlledCommandFactory controlledCommandFactory() {
        return this.controlledCommandFactory;
    }

    public JwzSupportedCommandParser supportedCommandParser() {
        return this.supportedCommandParser;
    }

    public SupportedCommandDispatcher supportedCommandDispatcher() {
        return this.supportedCommandDispatcher;
    }
}
