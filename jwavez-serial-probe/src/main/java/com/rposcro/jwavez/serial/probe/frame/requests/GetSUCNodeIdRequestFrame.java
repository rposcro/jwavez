package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_SUC_NODE_ID)
public class GetSUCNodeIdRequestFrame extends SOFRequestFrame {

  public GetSUCNodeIdRequestFrame() {
    super(SerialCommand.GET_SUC_NODE_ID);
  }
}
