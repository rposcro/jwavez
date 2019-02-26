package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_LEARN_MODE;

import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SetLearnModeRequest extends ZWaveRequest {

  public static SerialRequest createSerialRequest(LearnMode learnMode, byte callbackFunctionId) {
    return SerialRequest.builder()
        .responseExpected(true)
        .serialCommand(SET_LEARN_MODE)
        .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 2, SET_LEARN_MODE)
            .put(learnMode.getCode())
            .put(callbackFunctionId)
            .putCRC())
        .callbackFunctionId(callbackFunctionId)
        .build();
  }
}
