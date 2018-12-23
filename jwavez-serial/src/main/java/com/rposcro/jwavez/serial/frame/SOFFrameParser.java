package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.exceptions.FrameException;
import com.rposcro.jwavez.serial.frame.constants.FrameType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SOFFrameParser {

  private SOFFrameRegistry frameRegistry;

  public SOFFrame parseFrame(byte[] buffer) throws FrameException {
    if (buffer[SOFFrame.OFFSET_FRAME_TYPE] == FrameType.RES.getCode()) {
      return instantiateFrame(buffer, frameRegistry.responseClass(buffer[3]).get());
    } else { // don't know unique way to distinquish between callback and request, assuming it's always callback as for now
      return instantiateFrame(buffer, frameRegistry.callbackClass(buffer[3]).get());
    }
  }

  public SOFCallbackFrame parseCallbackFrame(byte[] buffer) throws FrameException {
    validateCallbackFrame(buffer);
    return instantiateFrame(buffer, frameRegistry.callbackClass(buffer[3]).get());
  }

  public SOFResponseFrame parseResponseFrame(byte[] buffer) throws FrameException {
    validateResponseFrame(buffer);
    return instantiateFrame(buffer, frameRegistry.responseClass(buffer[3]).get());
  }

  public SOFRequestFrame parseRequestFrame(byte[] buffer)  throws FrameException {
    validateRequestFrame(buffer);
    return instantiateFrame(buffer, frameRegistry.requestClass(buffer[3]).get());
  }

  private <T> T instantiateFrame(byte[] buffer, Class<T> frameClass) throws FrameException {
    try {
      if (frameClass == null) {
        throw new FrameException("No request frame for serialCommand " + buffer[3]);
      }
      T frame = frameClass.getConstructor(byte[].class).newInstance(buffer);
      return frame;
    } catch(Exception e) {
      throw new FrameException(e);
    }
  }

  private void validateCallbackFrame(byte[] buffer) {
    if (!FrameType.REQ.matchesCode(buffer[2])) {
      throw new FrameException("Expected callback frame while received " + buffer[2]);
    }
  }

  private void validateRequestFrame(byte[] buffer) {
    if (!FrameType.REQ.matchesCode(buffer[2])) {
      throw new FrameException("Expected request frame while received " + buffer[2]);
    }
  }

  private void validateResponseFrame(byte[] buffer) {
    if (!FrameType.RES.matchesCode(buffer[2])) {
      throw new FrameException("Expected response frame while received " + buffer[2]);
    }
  }
}
