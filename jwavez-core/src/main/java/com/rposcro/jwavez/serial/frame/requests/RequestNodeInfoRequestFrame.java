package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.REQUEST_NODE_INFO)
public class RequestNodeInfoRequestFrame extends SOFRequestFrame {

  public RequestNodeInfoRequestFrame(byte nodeId) {
    super(SerialCommand.REQUEST_NODE_INFO, nodeId);
  }
}
