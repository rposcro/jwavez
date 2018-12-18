package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.ENABLE_SUC)
public class EnableSUCResponseFrame extends SOFResponseFrame {

  public EnableSUCResponseFrame(byte[] buffer) {
    super(buffer);
  }
}
