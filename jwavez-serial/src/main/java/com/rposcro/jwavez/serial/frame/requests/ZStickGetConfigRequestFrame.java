package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.zstick.ZStickConfigParameter;

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