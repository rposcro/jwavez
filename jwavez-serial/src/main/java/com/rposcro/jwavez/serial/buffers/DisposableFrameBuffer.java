package com.rposcro.jwavez.serial.buffers;

import com.rposcro.jwavez.serial.utils.FrameUtil;
import java.nio.ByteBuffer;

public final class DisposableFrameBuffer implements FrameBuffer {

  private ByteBuffer dataBuffer;

  public DisposableFrameBuffer(int capacity) {
    this.dataBuffer = ByteBuffer.allocateDirect(capacity);
  }

  public DisposableFrameBuffer putCRC() {
    dataBuffer.put(FrameUtil.frameCRC(asByteBuffer()));
    return this;
  }

  @Override
  public DisposableFrameBuffer put(byte data) {
    dataBuffer.put(data);
    return this;
  }

  @Override
  public ByteBuffer asByteBuffer() {
    ByteBuffer cloned = dataBuffer.asReadOnlyBuffer();
    cloned.position(0);
    return cloned;
  }
}
