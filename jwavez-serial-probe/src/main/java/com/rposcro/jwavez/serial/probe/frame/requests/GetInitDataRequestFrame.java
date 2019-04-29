package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_INIT_DATA)
public class GetInitDataRequestFrame extends SOFRequestFrame {

  public GetInitDataRequestFrame() {
    super(GetInitDataRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
