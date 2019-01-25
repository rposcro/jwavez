package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.SET_DEFAULT)
public class SetDefaultRequestFrame extends SOFRequestFrame {

  public SetDefaultRequestFrame(byte callbackId) {
    super(SetDefaultRequestFrame.class.getAnnotation(RequestFrameModel.class).function(),
        callbackId);
  }
}
