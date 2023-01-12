package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.model.FrameCast;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandInterceptor implements CallbackInterceptor {

    private SupportedCommandParser supportedCommandParser;
    private SupportedCommandDispatcher supportedCommandDispatcher;

    private boolean supportMulticasts;
    private boolean supportBroadcasts;
    private boolean skipUnsupportedCallbacks;

    public ApplicationCommandInterceptor() {
        this.supportedCommandDispatcher = new SupportedCommandDispatcher();
        this.supportedCommandParser = SupportedCommandParser.defaultParser();
    }

    @Builder
    public ApplicationCommandInterceptor(
            SupportedCommandDispatcher supportedCommandDispatcher,
            boolean supportMulticasts,
            boolean supportBroadcasts,
            boolean skipUnsupportedCallbacks) {
        this.supportedCommandParser = SupportedCommandParser.defaultParser();
        this.supportedCommandDispatcher = supportedCommandDispatcher;
        this.supportBroadcasts = supportBroadcasts;
        this.supportMulticasts = supportMulticasts;
        this.skipUnsupportedCallbacks = skipUnsupportedCallbacks;
    }

    public ApplicationCommandInterceptor registerCommandHandler(CommandType commandType, SupportedCommandHandler commandHandler) {
        supportedCommandDispatcher.registerHandler(commandType, commandHandler);
        return this;
    }

    public ApplicationCommandInterceptor registerAllCommandsHandler(SupportedCommandHandler commandHandler) {
        supportedCommandDispatcher.registerAllCommandsHandler(commandHandler);
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
                log.debug("Skipped {} callback", commandCallback.getRxStatus().getFrameCast());
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Skipped callback frame: {}", callback.getSerialCommand());
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
