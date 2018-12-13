package com.rposcro.jwavez.serial.frame.responses;

import static com.rposcro.jwavez.serial.utils.FieldUtil.*;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdResponseFrame extends SOFResponseFrame {

  private boolean status;

  public SetSUCNodeIdResponseFrame(byte[] buffer) {
    super(buffer);
    this.status = byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
