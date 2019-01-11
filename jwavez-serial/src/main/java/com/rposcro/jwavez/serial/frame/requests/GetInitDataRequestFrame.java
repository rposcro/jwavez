package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_INIT_DATA)
public class GetInitDataRequestFrame extends SOFRequestFrame {

  public GetInitDataRequestFrame() {
    super(GetInitDataRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
