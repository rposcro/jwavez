package com.rposcro.jwavez.serial.interceptors;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.frame.callbacks.ApplicationCommandHandlerCallbackFrame;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptor;
import com.rposcro.jwavez.serial.rxtx.InboundFrameInterceptorContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class ApplicationCommandDispatcher implements InboundFrameInterceptor {

  private SupportedCommandParser supportedCommandParser;
  private SupportedCommandDispatcher supportedCommandDispatcher;

  @Override
  public void intercept(InboundFrameInterceptorContext context) {
    if (context.getFrame() instanceof ApplicationCommandHandlerCallbackFrame) {
      try {
        ApplicationCommandHandlerCallbackFrame commandFrame = (ApplicationCommandHandlerCallbackFrame) context.getFrame();
        ImmutableBuffer payload = ImmutableBuffer.overBuffer(commandFrame.getCommandPayload(), 0, commandFrame.getCommandLength());
        ZWaveSupportedCommand command = supportedCommandParser.parseCommand(payload);
        log.debug("Application Command Handler from {}. Command: {} {}", commandFrame.getSourceNodeId(), command.commandClass(), command.commandType());
        supportedCommandDispatcher.dispatchCommand(command);
      } catch(CommandNotSupportedException e) {
        log.warn("", e);
      }
    }
  }
}
