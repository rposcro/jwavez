package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.ENABLE_SUC)
public class EnableSUCResponseFrame extends SOFResponseFrame {

  public EnableSUCResponseFrame(byte[] buffer) {
    super(buffer);
  }
}
