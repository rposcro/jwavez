package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import lombok.Getter;

@Getter
public class UnknownCallback extends Callback {

  private byte[] payload;
  private byte crc;

  public UnknownCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    this.payload = frameBuffer.getBytes(frameBuffer.remaining() - 1);
    this.crc = frameBuffer.get();
  }
}
