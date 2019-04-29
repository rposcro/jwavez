package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.LearnMode;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeRequestFrame extends SOFRequestFrame {

  public SetLearnModeRequestFrame(LearnMode learnMode, byte callbackFunctionId) {
    super(SetLearnModeRequestFrame.class.getAnnotation(RequestFrameModel.class).function(),
        (byte) (learnMode.getCode()), callbackFunctionId);
  }
}
