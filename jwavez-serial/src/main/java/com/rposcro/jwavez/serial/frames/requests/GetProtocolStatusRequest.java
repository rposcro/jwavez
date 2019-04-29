package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_PROTOCOL_STATUS;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetProtocolStatusRequest extends ZWaveRequest {

  public static SerialRequest createGetProtocolStatusRequest() {
    return nonPayloadRequest(GET_PROTOCOL_STATUS);
  }
}
