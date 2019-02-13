package com.rposcro.jwavez.serial.frames;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.exceptions.FrameParseException;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.callbacks.UnknownCallback;
import com.rposcro.jwavez.serial.frames.responses.ZWaveResponse;
import com.rposcro.jwavez.serial.frames.responses.UnknownResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundFrameParser {

  private FramesModelRegistry frameRegistry;

  public InboundFrameParser() {
    this.frameRegistry = FramesModelRegistry.defaultRegistry();
  }

  public ZWaveCallback parseCallbackFrame(ViewBuffer buffer) throws FrameParseException {
    validateCallbackFrame(buffer);
    return instantiateCallbackFrame(buffer);
  }

  public ZWaveResponse parseResponseFrame(ViewBuffer buffer) throws FrameParseException {
    validateResponseFrame(buffer);
    return instantiateResponseFrame(buffer);
  }

  private ZWaveCallback instantiateCallbackFrame(ViewBuffer buffer) throws FrameParseException {
    try {
      Class<? extends ZWaveCallback> clazz = frameRegistry.callbackClass(buffer.get(FRAME_OFFSET_COMMAND))
          .orElse(UnknownCallback.class);
      ZWaveCallback frame = clazz.getConstructor(ViewBuffer.class).newInstance(buffer);
      return frame;
    } catch(Exception e) {
      throw new FrameParseException(e);
    }
  }

  private ZWaveResponse instantiateResponseFrame(ViewBuffer buffer) throws FrameParseException {
    try {
      Class<? extends ZWaveResponse> clazz = frameRegistry.responseClass(buffer.get(FRAME_OFFSET_COMMAND))
          .orElse(UnknownResponse.class);
      ZWaveResponse frame = clazz.getConstructor(ViewBuffer.class).newInstance(buffer);
      return frame;
    } catch(Exception e) {
      throw new FrameParseException(e);
    }
  }

  private void validateCallbackFrame(ViewBuffer buffer)  throws FrameParseException {
    if (TYPE_REQ != buffer.get(FRAME_OFFSET_TYPE)) {
      throw new FrameParseException("Expected callback frame while received " + buffer.get(FRAME_OFFSET_TYPE));
    }
  }

  private void validateResponseFrame(ViewBuffer buffer) {
    if (TYPE_RES != buffer.get(FRAME_OFFSET_TYPE)) {
      throw new FatalSerialException("Expected request frame while received " + buffer.get(FRAME_OFFSET_TYPE));
    }
  }
}
