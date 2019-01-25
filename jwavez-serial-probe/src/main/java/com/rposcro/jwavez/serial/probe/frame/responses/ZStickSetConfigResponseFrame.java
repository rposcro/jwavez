package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;

@ResponseFrameModel(function = SerialCommand.ZSTICK_SET_CONFIG)
public class ZStickSetConfigResponseFrame extends SOFResponseFrame {

  public ZStickSetConfigResponseFrame(byte[] buffer) {
    super(buffer);
  }
}
