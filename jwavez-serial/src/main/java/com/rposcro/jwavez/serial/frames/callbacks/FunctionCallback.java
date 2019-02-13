package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import lombok.Getter;

@Getter
public abstract class FunctionCallback extends ZWaveCallback {

  private byte functionCallId;

  public FunctionCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    functionCallId = frameBuffer.get();
  }
}