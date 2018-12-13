package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.zstick.ZStickConfigParameter;
import java.util.HashMap;
import java.util.Map;

@ResponseFrameModel(function = SerialCommand.ZSTICK_GET_CONFIG)
public class ZStickGetConfigResponseFrame extends SOFResponseFrame {

  private static final int KEY_LENGTH = 16;

  private Map<Byte, byte[]> parameterMap;

  public ZStickGetConfigResponseFrame(byte[] buffer) {
    super(buffer);
    this.parameterMap = new HashMap<>();
    int payloadLength = buffer[OFFSET_LENGTH] - 3;

    for (int idx = 1; idx < payloadLength; ) { // skipping first payload byte (length of all parameters section)
      byte paramCode = buffer[OFFSET_PAYLOAD + (idx++)];
      int paramSize = ((int) buffer[OFFSET_PAYLOAD + (idx++)]) & 0xFF;
      byte[] paramValue = new byte[paramSize];
      System.arraycopy(buffer, OFFSET_PAYLOAD + idx, paramValue, 0, paramSize);
      parameterMap.put(paramCode, paramValue);
      idx += paramSize;
    }
  }

  public byte[] getParameterValue(ZStickConfigParameter parameter) {
    return parameterMap.get(parameter.getParameterCode());
  }
}
