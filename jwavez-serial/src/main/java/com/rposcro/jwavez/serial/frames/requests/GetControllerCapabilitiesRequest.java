package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_CONTROLLER_CAPABILITIES;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetControllerCapabilitiesRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest() {
    return nonPayloadRequest(GET_CONTROLLER_CAPABILITIES);
  }
}
