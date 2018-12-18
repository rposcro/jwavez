package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_VERSION)
public class GetVersionRequestFrame extends SOFRequestFrame {

  public GetVersionRequestFrame() {
    super(SerialCommand.GET_VERSION);
  }
}
