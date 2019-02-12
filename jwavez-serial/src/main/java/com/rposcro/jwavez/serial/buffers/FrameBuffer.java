package com.rposcro.jwavez.serial.buffers;

import java.nio.ByteBuffer;

public interface FrameBuffer {

  FrameBuffer put(byte data);

  ByteBuffer asByteBuffer();

  default void release() {
  }
}
