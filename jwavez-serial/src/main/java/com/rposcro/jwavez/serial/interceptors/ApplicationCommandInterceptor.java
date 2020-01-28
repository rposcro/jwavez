package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.enums.CommandType;
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

  public ApplicationCommandInterceptor() {
    this.supportedCommandDispatcher = new SupportedCommandDispatcher();
    this.supportedCommandParser = SupportedCommandParser.defaultParser();
  }

  @Builder
  public ApplicationCommandInterceptor(
      SupportedCommandDispatcher supportedCommandDispatcher,
      boolean supportMulticasts,
      boolean supportBroadcasts) {
    this.supportedCommandParser = SupportedCommandParser.defaultParser();
    this.supportedCommandDispatcher = supportedCommandDispatcher;
    this.supportBroadcasts = supportBroadcasts;
    this.supportMulticasts = supportMulticasts;
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
        ZWaveSupportedCommand command = supportedCommandParser.parseCommand(
            ImmutableBuffer.overBuffer(commandCallback.getCommandPayload()), commandCallback.getSourceNodeId());
        supportedCommandDispatcher.dispatchCommand(command);
      } else {
        log.debug("Skipped {} callback", commandCallback.getRxStatus().getFrameCast());
      }
    } else if (log.isDebugEnabled()) {
      log.debug("Skipped frame: {}", callback.getSerialCommand());
    }
  }

  private boolean isCastSupported(ApplicationCommandHandlerCallback callback) {
    FrameCast frameCast = callback.getRxStatus().getFrameCast();
    return frameCast == FrameCast.SINGLE
        || (frameCast == FrameCast.BROADCAST && supportBroadcasts)
        || (frameCast == FrameCast.MULTICAST && supportMulticasts);
  }
}
