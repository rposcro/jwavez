package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.FrameCategory;
import com.rposcro.jwavez.serial.enums.FrameType;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_CATEGORY;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_COMMAND;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_LENGTH;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_TYPE;
import static java.lang.String.format;

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
    return FrameCategory.ofCode(buffer.get(FRAME_OFFSET_CATEGORY));
  }

  public static byte categoryCode(ViewBuffer buffer) {
    return buffer.get(FRAME_OFFSET_CATEGORY);
  }

  public static FrameType type(ViewBuffer buffer) {
    return FrameType.ofCode(buffer.get(FRAME_OFFSET_TYPE));
  }

  public static byte typeCode(ViewBuffer buffer) {
    return buffer.get(FRAME_OFFSET_TYPE);
  }

  public static byte length(ViewBuffer buffer) {
    return buffer.get(FRAME_OFFSET_LENGTH);
  }

  public static SerialCommand serialCommand(ViewBuffer buffer) {
    return SerialCommand.ofCode(buffer.get(FRAME_OFFSET_COMMAND));
  }

  public static byte serialCommandCode(ViewBuffer buffer) {
    return buffer.get(FRAME_OFFSET_COMMAND);
  }

  public static String asFineString(ViewBuffer buffer) {
    StringBuffer frameString = new StringBuffer();
    int length = length(buffer);

    frameString.append(format("%s(%02x) Len(%02x) %s(%02x) %s(%02x) ",
            category(buffer), categoryCode(buffer),
            (byte) length,
            type(buffer), typeCode(buffer),
            serialCommand(buffer), serialCommandCode(buffer)
            ));

    String payloadString = IntStream.range(FRAME_OFFSET_PAYLOAD, buffer.length() - 1)
            .mapToObj(idx -> format("%02x", buffer.get(idx)))
            .collect(Collectors.joining(" "));
    frameString.append("Payload(" + payloadString + ") ");

    frameString.append(format("CRC(%02x)", buffer.get(buffer.length() - 1)));

    return frameString.toString();
  }

  public static void main(String[] args) {
    byte[] bytes = new byte[] {
            0x01, 0x05, 0x00, 0x13, 0x5c, 0x00, (byte) 0xb5
    };
    ViewBuffer buffer = new ViewBuffer(ByteBuffer.wrap(bytes));
    System.out.println(asFineString(buffer));
  }
}
