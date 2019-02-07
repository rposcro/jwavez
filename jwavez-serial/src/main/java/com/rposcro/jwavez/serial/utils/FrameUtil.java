package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import java.nio.ByteBuffer;

public class FrameUtil {

  public static byte frameCRC(byte[] frameBuffer) {
    byte crc = (byte) 0xff;
    for (int idx = 1; idx < frameBuffer.length - 1; idx++) {
      crc ^= frameBuffer[idx];
    }
    return crc;
  }

  public static byte frameCRC(ByteBuffer buffer) {
    byte crc = (byte) 0xff;
    for (int idx = 1; idx < buffer.limit() - 1; idx++) {
      crc ^= buffer.get(idx);
    }
    return crc;
  }

  public static byte frameCRC(ViewBuffer buffer) {
    byte crc = (byte) 0xff;
    for (int idx = 1; idx < buffer.length() - 1; idx++) {
      crc ^= buffer.get(idx);
    }
    return crc;
  }
}
