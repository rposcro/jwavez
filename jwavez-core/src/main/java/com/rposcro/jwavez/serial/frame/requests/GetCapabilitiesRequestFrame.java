package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_CAPABILITIES)
public class GetCapabilitiesRequestFrame extends SOFRequestFrame {

  public GetCapabilitiesRequestFrame() {
    super(SerialCommand.GET_CAPABILITIES);
  }
}
