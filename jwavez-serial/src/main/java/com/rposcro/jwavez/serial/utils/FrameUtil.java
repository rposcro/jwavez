package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.FrameCategory;
import com.rposcro.jwavez.serial.enums.FrameType;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
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

  public static FrameCategory category(ViewBuffer buffer) {
    return FrameCategory.ofCode(buffer.get(SerialFrameConstants.FRAME_OFFSET_CATEGORY));
  }

  public static byte categoryCode(ViewBuffer buffer) {
    return buffer.get(SerialFrameConstants.FRAME_OFFSET_CATEGORY);
  }

  public static FrameType type(ViewBuffer buffer) {
    return FrameType.ofCode(buffer.get(SerialFrameConstants.FRAME_OFFSET_TYPE));
  }

  public static byte typeCode(ViewBuffer buffer) {
    return buffer.get(SerialFrameConstants.FRAME_OFFSET_TYPE);
  }

  public static SerialCommand serialCommand(ViewBuffer buffer) {
    return SerialCommand.ofCode(buffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND));
  }

  public static byte serialCommandCode(ViewBuffer buffer) {
    return buffer.get(SerialFrameConstants.FRAME_OFFSET_COMMAND);
  }
}
