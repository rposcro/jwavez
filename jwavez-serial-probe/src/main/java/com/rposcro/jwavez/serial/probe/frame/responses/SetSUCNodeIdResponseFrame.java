package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.probe.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdResponseFrame extends SOFResponseFrame {

  private boolean requestAccepted;

  public SetSUCNodeIdResponseFrame(byte[] buffer) {
    super(buffer);
    this.requestAccepted = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
