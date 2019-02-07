package com.rposcro.jwavez.serial.frames;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_REQ;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.TYPE_RES;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;
import com.rposcro.jwavez.serial.frames.callbacks.Callback;
import com.rposcro.jwavez.serial.frames.callbacks.UnknownCallback;
import com.rposcro.jwavez.serial.frames.responses.Response;
import com.rposcro.jwavez.serial.frames.responses.UnknownResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class InboundFrameParser {

  private FramesModelRegistry frameRegistry;

  public Callback parseCallbackFrame(ViewBuffer buffer) {
    validateCallbackFrame(buffer);
    return instantiateCallbackFrame(buffer);
  }

  public Response parseResponseFrame(ViewBuffer buffer) {
    validateResponseFrame(buffer);
    return instantiateResponseFrame(buffer);
  }

  private Callback instantiateCallbackFrame(ViewBuffer buffer) {
    try {
      Class<? extends Callback> clazz = frameRegistry.callbackClass(buffer.get(FRAME_OFFSET_COMMAND))
          .orElse(UnknownCallback.class);
      Callback frame = clazz.getConstructor(ViewBuffer.class).newInstance(buffer);
      return frame;
    } catch(Exception e) {
      throw new FatalSerialException(e);
    }
  }

  private Response instantiateResponseFrame(ViewBuffer buffer) {
    try {
      Class<? extends Response> clazz = frameRegistry.responseClass(buffer.get(FRAME_OFFSET_COMMAND))
          .orElse(UnknownResponse.class);
      Response frame = clazz.getConstructor(ViewBuffer.class).newInstance(buffer);
      return frame;
    } catch(Exception e) {
      throw new FatalSerialException(e);
    }
  }

  private void validateCallbackFrame(ViewBuffer buffer) {
    if (TYPE_REQ != buffer.get(FRAME_OFFSET_TYPE)) {
      throw new FatalSerialException("Expected callback frame while received " + buffer.get(FRAME_OFFSET_TYPE));
    }
  }

  private void validateResponseFrame(ViewBuffer buffer) {
    if (TYPE_RES != buffer.get(FRAME_OFFSET_TYPE)) {
      throw new FatalSerialException("Expected request frame while received " + buffer.get(FRAME_OFFSET_TYPE));
    }
  }
}
