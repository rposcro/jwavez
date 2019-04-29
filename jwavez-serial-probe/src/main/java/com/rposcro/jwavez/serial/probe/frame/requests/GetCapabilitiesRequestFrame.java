package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_CAPABILITIES)
public class GetCapabilitiesRequestFrame extends SOFRequestFrame {

  public GetCapabilitiesRequestFrame() {
    super(SerialCommand.GET_CAPABILITIES);
  }
}
