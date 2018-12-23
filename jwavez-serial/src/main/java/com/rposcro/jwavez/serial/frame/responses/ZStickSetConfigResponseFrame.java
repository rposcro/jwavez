package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;

@ResponseFrameModel(function = SerialCommand.ZSTICK_SET_CONFIG)
public class ZStickSetConfigResponseFrame extends SOFResponseFrame {

  public ZStickSetConfigResponseFrame(byte[] buffer) {
    super(buffer);
  }
}
