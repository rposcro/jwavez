package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.REQUEST_NODE_INFO)
public class RequestNodeInfoRequestFrame extends SOFRequestFrame {

  public RequestNodeInfoRequestFrame(byte nodeId) {
    super(SerialCommand.REQUEST_NODE_INFO, nodeId);
  }

  public RequestNodeInfoRequestFrame(NodeId nodeId) {
    super(SerialCommand.REQUEST_NODE_INFO, nodeId.getId());
  }
}
