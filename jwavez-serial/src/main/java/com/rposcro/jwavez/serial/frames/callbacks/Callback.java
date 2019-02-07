package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import lombok.Getter;

@Getter
public abstract class Callback {

  private SerialCommand serialCommand;
  private int length;

  public Callback(ViewBuffer viewBuffer) {
    this.length = viewBuffer.get(SerialFrameConstants.FRAME_OFFSET_LENGTH) & 0xff;
    this.serialCommand = SerialCommand.ofCode(viewBuffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND));
  }
}