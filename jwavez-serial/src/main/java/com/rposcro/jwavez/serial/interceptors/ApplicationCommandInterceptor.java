package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.listeners.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.listeners.SupportedCommandListener;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.model.FrameCast;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandInterceptor implements CallbackInterceptor {

    private JwzSupportedCommandParser supportedCommandParser;
    private SupportedCommandDispatcher supportedCommandDispatcher;

    private boolean supportMulticasts;
    private boolean supportBroadcasts;
    private boolean skipUnsupportedCallbacks;

    public ApplicationCommandInterceptor() {
        this.supportedCommandDispatcher = new SupportedCommandDispatcher();
        this.supportedCommandParser = new JwzSupportedCommandParser(SupportedCommandResolversRegistry.instance());
    }

    @Builder
    public ApplicationCommandInterceptor(
            SupportedCommandDispatcher supportedCommandDispatcher,
            boolean supportMulticasts,
            boolean supportBroadcasts,
            boolean skipUnsupportedCallbacks) {
        this.supportedCommandParser = new JwzSupportedCommandParser(SupportedCommandResolversRegistry.instance());
        this.supportedCommandDispatcher = supportedCommandDispatcher;
        this.supportBroadcasts = supportBroadcasts;
        this.supportMulticasts = supportMulticasts;
        this.skipUnsupportedCallbacks = skipUnsupportedCallbacks;
    }

    public ApplicationCommandInterceptor registerCommandListener(CommandType commandType, SupportedCommandListener commandListener) {
        supportedCommandDispatcher.registerListener(commandType, commandListener);
        return this;
    }

    public ApplicationCommandInterceptor registerCommandsListener(SupportedCommandListener commandHandler) {
        supportedCommandDispatcher.registerCommandsListener(commandHandler);
        return this;
    }

    @Override
    public void intercept(ZWaveCallback callback) {
        if (callback instanceof ApplicationCommandHandlerCallback) {
            ApplicationCommandHandlerCallback commandCallback = (ApplicationCommandHandlerCallback) callback;
            if (isCastSupported(commandCallback)) {
                if (skipUnsupportedCallbacks) {
                    if (supportedCommandParser.isCommandSupported(ImmutableBuffer.overBuffer(commandCallback.getCommandPayload()))) {
                        parseAndDispatchCallback(commandCallback);
                    } else {
                        log.warn("Skipped unsupported application command");
                    }
                } else {
                    parseAndDispatchCallback(commandCallback);
                }
            } else {
                log.debug("Skipped {} callback, cast not supported", commandCallback.getRxStatus().getFrameCast());
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Skipped callback frame: {}", callback.asFineString());
        }
    }

    private void parseAndDispatchCallback(ApplicationCommandHandlerCallback commandCallback) {
        ZWaveSupportedCommand command = supportedCommandParser.parseCommand(
                ImmutableBuffer.overBuffer(commandCallback.getCommandPayload()), commandCallback.getSourceNodeId());
        supportedCommandDispatcher.dispatchCommand(command);
    }

    private boolean isCastSupported(ApplicationCommandHandlerCallback callback) {
        FrameCast frameCast = callback.getRxStatus().getFrameCast();
        return frameCast == FrameCast.SINGLE
                || (frameCast == FrameCast.BROADCAST && supportBroadcasts)
                || (frameCast == FrameCast.MULTICAST && supportMulticasts);
    }
}
