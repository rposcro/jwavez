package com.rposcro.jwavez.serial.probe.utils;

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
    buffer.get();
    for (int idx = 1; idx < buffer.getLength() - 1; idx++) {
      crc ^= buffer.get(idx);
    }
    return crc;
  }

  public static String bufferToString(ByteBuffer buffer) {
    StringBuffer string = new StringBuffer();
    int position = buffer.getPosition();
    buffer.rewind();
    for (int i = 0; i < buffer.getLength(); i++) {
      string.append(String.format("%02x ", ((int) buffer.get()) & 0xff));
    }
    buffer.setPosition(position);
    return string.toString();
  }

  public static String bufferToString(byte[] buffer) {
    StringBuffer string = new StringBuffer();
    for (byte bt: buffer) {
      string.append(String.format("%02x ", ((int) bt) & 0xff));
    }
    return string.toString();
  }
}
