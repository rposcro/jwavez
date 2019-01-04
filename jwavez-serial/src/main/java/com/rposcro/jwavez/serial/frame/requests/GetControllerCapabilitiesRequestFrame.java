package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_CONTROLLER_CAPABILITIES)
public class GetControllerCapabilitiesRequestFrame extends SOFRequestFrame {

  public GetControllerCapabilitiesRequestFrame() {
    super(GetControllerCapabilitiesRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
