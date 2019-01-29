package com.rposcro.jwavez.serial.utils;

import java.util.stream.IntStream;

public class BufferUtil {

  public static String bufferToString(ViewBuffer buffer) {
    StringBuffer string = new StringBuffer();
    IntStream.range(buffer.position(), buffer.length())
        .forEach(index -> string.append(String.format("%02x ", buffer.get(index))));
    return string.toString();
  }

}
