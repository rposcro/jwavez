package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.buffers.dispatchers.SingletonBufferDispatcher;
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

  public static FrameBuffer frameBufferFromData(List<Integer> data) {
    SingletonBufferDispatcher dispatcher = new SingletonBufferDispatcher();
    FrameBuffer frameBuffer = dispatcher.allocateBuffer(data.size());
    data.forEach(value -> frameBuffer.put(value.byteValue()));
    return frameBuffer;
  }

  public static ByteBuffer byteBufferFromData(List<Integer> data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.size());
    data.forEach(value -> byteBuffer.put(value.byteValue()));
    byteBuffer.position(0);
    return byteBuffer;
  }

  public static byte[] asByteArray(List<Integer> bytesList) {
    byte[] bytes = new byte[bytesList.size()];
    int idx = 0;
    for (Integer val : bytesList) {
      bytes[idx++] = val.byteValue();
    }
    return bytes;
  }

}
