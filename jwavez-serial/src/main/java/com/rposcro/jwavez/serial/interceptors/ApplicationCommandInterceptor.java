package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandInterceptor implements CallbackInterceptor {

  private SupportedCommandParser supportedCommandParser;
  private SupportedCommandDispatcher supportedCommandDispatcher;

  @Builder
  public ApplicationCommandInterceptor(SupportedCommandDispatcher supportedCommandDispatcher) {
    this.supportedCommandDispatcher = supportedCommandDispatcher;
    this.supportedCommandParser = SupportedCommandParser.defaultParser();
  }

  @Override
  public void intercept(ZWaveCallback callback) {
    if (callback instanceof ApplicationCommandHandlerCallback) {
      ApplicationCommandHandlerCallback commandCallback = (ApplicationCommandHandlerCallback) callback;
      ZWaveSupportedCommand command = supportedCommandParser.parseCommand(
          ImmutableBuffer.overBuffer(commandCallback.getCommandPayload()), commandCallback.getSourceNodeId());
      supportedCommandDispatcher.dispatchCommand(command);
    } else if (log.isDebugEnabled()) {
      log.debug("Skipped frame: {}", callback.getSerialCommand());
    }
  }
}
