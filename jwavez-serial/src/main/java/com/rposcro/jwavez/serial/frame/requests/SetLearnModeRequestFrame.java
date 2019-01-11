package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.LearnMode;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeRequestFrame extends SOFRequestFrame {

  public SetLearnModeRequestFrame(LearnMode learnMode, byte callbackFunctionId) {
    super(SetLearnModeRequestFrame.class.getAnnotation(RequestFrameModel.class).function(),
        (byte) (learnMode.getCode()), callbackFunctionId);
  }
}
