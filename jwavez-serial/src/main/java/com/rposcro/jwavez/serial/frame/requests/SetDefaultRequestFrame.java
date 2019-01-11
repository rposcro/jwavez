package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.SET_DEFAULT)
public class SetDefaultRequestFrame extends SOFRequestFrame {

  public SetDefaultRequestFrame(byte callbackId) {
    super(SetDefaultRequestFrame.class.getAnnotation(RequestFrameModel.class).function(),
        callbackId);
  }
}
