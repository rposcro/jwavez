package com.rposcro.jwavez.serial.frame.requests;

import static com.rposcro.jwavez.serial.utils.FieldUtil.*;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdRequestFrame extends SOFRequestFrame {

  public SetSUCNodeIdRequestFrame(byte nodeId, boolean sucState, boolean enableSIS) {
    super(SerialCommand.SET_SUC_NODE_ID,
        nodeId, booleanByte(sucState), booleanByte(false), booleanByte(enableSIS), (byte) 0xFE);
  }

  private byte toByte(boolean flag) {
    return (byte) (flag ? 1 : 0);
  }
}
