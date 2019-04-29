package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.RF_POWER_LEVEL_GET)
public class GetRFPowerLevelRequestFrame extends SOFRequestFrame {

  public GetRFPowerLevelRequestFrame() {
    super(GetRFPowerLevelRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
