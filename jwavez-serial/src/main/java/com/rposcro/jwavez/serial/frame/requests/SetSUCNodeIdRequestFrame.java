package com.rposcro.jwavez.serial.frame.requests;

import static com.rposcro.jwavez.serial.utils.FieldUtil.*;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import lombok.Getter;

@Getter
@RequestFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdRequestFrame extends SOFRequestFrame {

  private boolean localCall;

  public SetSUCNodeIdRequestFrame(NodeId nodeId, boolean enableSucAndSis, byte callbackFunctionId) {
    super(SerialCommand.SET_SUC_NODE_ID,
        nodeId.getId(), booleanByte(enableSucAndSis), booleanByte(false), booleanByte(enableSucAndSis), callbackFunctionId);
  }
}
