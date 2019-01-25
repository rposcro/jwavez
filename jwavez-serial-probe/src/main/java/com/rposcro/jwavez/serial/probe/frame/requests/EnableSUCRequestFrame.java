package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.ENABLE_SUC)
public class EnableSUCRequestFrame extends SOFRequestFrame {

  public EnableSUCRequestFrame() {
    super(SerialCommand.ENABLE_SUC);
  }
}
