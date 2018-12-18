package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_SUC_NODE_ID)
public class GetSUCNodeIdResponseFrame extends SOFResponseFrame {

  private byte sucNodeId;

  public GetSUCNodeIdResponseFrame(byte[] buffer) {
    super(buffer);
    this.sucNodeId = buffer[OFFSET_PAYLOAD];
  }
}
