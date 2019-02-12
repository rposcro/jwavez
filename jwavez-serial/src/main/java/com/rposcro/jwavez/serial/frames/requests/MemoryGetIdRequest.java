package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.MEMORY_GET_ID;

import com.rposcro.jwavez.serial.rxtx.FrameRequest;

public class MemoryGetIdRequest extends ZWaveRequest {

  public static FrameRequest createFrameRequest() {
    return nonPayloadRequest(MEMORY_GET_ID);
  }
}
