package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_CAPABILITIES;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetCapabilitiesRequest extends ZWaveRequest {

  public static SerialRequest createGetCapabilitiesRequest() {
    return nonPayloadRequest(GET_CAPABILITIES);
  }
}
