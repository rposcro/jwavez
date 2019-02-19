package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_VERSION;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetVersionRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest() {
    return nonPayloadRequest(GET_VERSION);
  }
}