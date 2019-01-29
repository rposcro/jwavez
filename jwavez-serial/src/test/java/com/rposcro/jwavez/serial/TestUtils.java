package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.utils.ViewBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

  public static List<Integer> dataFromBuffer(ViewBuffer viewBuffer) {
    List<Integer> data = new ArrayList<>(viewBuffer.remaining());
    while (viewBuffer.hasRemaining()) {
      data.add(viewBuffer.get() & 0xff);
    }
    return data;
  }

  public static ByteBuffer bufferFromData(List<Integer> data) {
    ByteBuffer dataBuffer = ByteBuffer.allocate(data.size());
    data.forEach(value -> dataBuffer.put(value.byteValue()));
    dataBuffer.position(0).limit(data.size());
    return dataBuffer;
  }
}
