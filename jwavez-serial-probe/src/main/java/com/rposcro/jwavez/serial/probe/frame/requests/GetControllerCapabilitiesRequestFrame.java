package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_CONTROLLER_CAPABILITIES)
public class GetControllerCapabilitiesRequestFrame extends SOFRequestFrame {

  public GetControllerCapabilitiesRequestFrame() {
    super(GetControllerCapabilitiesRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
