package com.rposcro.jwavez.serial.handlers;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.InboundFrameParser;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationCommandHandler implements Consumer<ViewBuffer> {

  private final static byte APP_COMMAND_CODE = SerialCommand.APPLICATION_COMMAND_HANDLER.getCode();

  private InboundFrameParser frameParser;
  private SupportedCommandParser supportedCommandParser;
  private SupportedCommandDispatcher supportedCommandDispatcher;

  @Builder
  public ApplicationCommandHandler(SupportedCommandDispatcher supportedCommandDispatcher) {
    this.supportedCommandDispatcher = supportedCommandDispatcher;
    this.supportedCommandParser = SupportedCommandParser.defaultParser();
    this.frameParser = InboundFrameParser.defaultParser();
  }

  @Override
  public void accept(ViewBuffer buffer) {
    if (buffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND) == APP_COMMAND_CODE) {
      try {
        ApplicationCommandHandlerCallback callback = (ApplicationCommandHandlerCallback) frameParser.parseCallbackFrame(buffer);
        ZWaveSupportedCommand command = supportedCommandParser.parseCommand(ImmutableBuffer.overBuffer(callback.getCommandPayload()), callback.getSourceNodeId());
        supportedCommandDispatcher.dispatchCommand(command);
      } catch(FrameParseException e) {
        log.error("Failed to parse application command handler frame: {}", BufferUtil.bufferToString(buffer));
      }
    } else if (log.isDebugEnabled()) {
      log.debug("Skipped frame: {}", BufferUtil.bufferToString(buffer));
    }
  }
}
