package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_VERSION)
public class GetVersionRequestFrame extends SOFRequestFrame {

  public GetVersionRequestFrame() {
    super(SerialCommand.GET_VERSION);
  }
}
