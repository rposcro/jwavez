package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.GET_SUC_NODE_ID)
public class GetSUCNodeIdRequestFrame extends SOFRequestFrame {

  public GetSUCNodeIdRequestFrame() {
    super(SerialCommand.GET_SUC_NODE_ID);
  }
}
