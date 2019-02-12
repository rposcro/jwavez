package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.GET_INIT_DATA;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetInitDataRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest() {
    return nonPayloadRequest(GET_INIT_DATA);
  }
}
