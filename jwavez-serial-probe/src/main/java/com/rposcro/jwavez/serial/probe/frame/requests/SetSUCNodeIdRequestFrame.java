package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;
import lombok.Getter;

@Getter
@RequestFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdRequestFrame extends SOFRequestFrame {

  private boolean localController;

  public SetSUCNodeIdRequestFrame(NodeId nodeId, boolean enableSucAndSis, byte callbackFunctionId) {
    super(SerialCommand.SET_SUC_NODE_ID,
        nodeId.getId(), FieldUtil.booleanByte(enableSucAndSis), FieldUtil.booleanByte(false), FieldUtil.booleanByte(enableSucAndSis), callbackFunctionId);
    localController = false;
  }

  public SetSUCNodeIdRequestFrame(NodeId nodeId, boolean enableSucAndSis) {
    super(SerialCommand.SET_SUC_NODE_ID,
        nodeId.getId(), FieldUtil.booleanByte(enableSucAndSis), FieldUtil.booleanByte(false), FieldUtil.booleanByte(enableSucAndSis), (byte) 0x00);
    localController = true;
  }
}
