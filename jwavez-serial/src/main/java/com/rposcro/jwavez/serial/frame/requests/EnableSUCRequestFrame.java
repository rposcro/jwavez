package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.ENABLE_SUC)
public class EnableSUCRequestFrame extends SOFRequestFrame {

  public EnableSUCRequestFrame() {
    super(SerialCommand.ENABLE_SUC);
  }
}
