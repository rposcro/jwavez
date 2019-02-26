package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_DEFAULT;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SetDefaultRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest(byte callbackFunctionId) {
    return SerialRequest.builder()
        .responseExpected(false)
        .serialCommand(SET_DEFAULT)
        .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 1, SET_DEFAULT)
            .put(callbackFunctionId)
            .putCRC())
        .callbackFunctionId(callbackFunctionId)
        .build();
  }
}
