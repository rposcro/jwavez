package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.utils.FieldUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeResponseFrame extends SOFResponseFrame {

  private boolean requestAccepted;

  public SetLearnModeResponseFrame(byte[] buffer) {
    super(buffer);
    requestAccepted = FieldUtil.byteBoolean(buffer[OFFSET_PAYLOAD]);
  }
}
