package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SEND_DATA_ABORT;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SendDataAbortRequest extends ZWaveRequest {

  public static SerialRequest createSendDataAbortRequest() {
    return nonPayloadRequest(SEND_DATA_ABORT);
  }
}
