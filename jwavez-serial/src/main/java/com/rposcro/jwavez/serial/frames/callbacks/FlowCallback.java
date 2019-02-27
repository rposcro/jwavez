package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import lombok.Getter;

@Getter
public abstract class FlowCallback extends ZWaveCallback {

  private byte callbackFlowId;

  public FlowCallback(ViewBuffer frameBuffer) {
    super(frameBuffer);
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    callbackFlowId = frameBuffer.get();
  }
}