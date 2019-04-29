package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.zstick.ZStickConfigParameter;

@RequestFrameModel(function = SerialCommand.ZSTICK_GET_CONFIG)
public class ZStickGetConfigRequestFrame extends SOFRequestFrame {

  public ZStickGetConfigRequestFrame(ZStickConfigParameter... parameters) {
    super(SerialCommand.ZSTICK_GET_CONFIG, toByteArray(parameters));
  }

  private static byte[] toByteArray(ZStickConfigParameter... parameters) {
    byte[] array = new byte[parameters.length];
    int idx = 0;
    for (ZStickConfigParameter param : parameters) {
      array[idx++] = param.getParameterCode();
    }
    return array;
  }

}