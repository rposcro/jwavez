package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
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
