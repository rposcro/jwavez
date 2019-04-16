package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.SET_LEARN_MODE;

import com.rposcro.jwavez.serial.model.LearnMode;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class SetLearnModeRequest extends ZWaveRequest {

  public static SerialRequest createSetLearnModeRequest(LearnMode learnMode, byte callbackFlowId) {
    return SerialRequest.builder()
        .responseExpected(false)
        .serialCommand(SET_LEARN_MODE)
        .frameData(startUpFrameBuffer(FRAME_CONTROL_SIZE + 2, SET_LEARN_MODE)
            .put(learnMode.getCode())
            .put(callbackFlowId)
            .putCRC())
        .callbackFlowId(callbackFlowId)
        .build();
  }
}
