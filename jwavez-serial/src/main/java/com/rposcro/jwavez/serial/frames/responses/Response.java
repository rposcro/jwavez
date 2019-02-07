package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import lombok.Getter;

@Getter
public class Response {

  private SerialCommand serialCommand;
  private int length;

  public Response(ViewBuffer viewBuffer) {
    this.length = viewBuffer.get(SerialFrameConstants.FRAME_OFFSET_LENGTH) & 0xff;
    this.serialCommand = SerialCommand.ofCode(viewBuffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND));
  }
}