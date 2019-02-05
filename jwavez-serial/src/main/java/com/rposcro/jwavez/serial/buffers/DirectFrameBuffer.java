package com.rposcro.jwavez.serial.buffers;

import com.rposcro.jwavez.serial.buffers.dispatchers.BufferDispatcher;
import java.nio.ByteBuffer;
import lombok.Builder;

@Builder
public final class DirectFrameBuffer implements FrameBuffer {

  private BufferDispatcher dispatcher;
  private ByteBuffer byteBuffer;

  DirectFrameBuffer(BufferDispatcher dispatcher, ByteBuffer byteBuffer) {
    this.dispatcher = dispatcher;
    this.byteBuffer = byteBuffer;
  }

  @Override
  public DirectFrameBuffer put(byte data) {
    byteBuffer.put(data);
    return this;
  }

  @Override
  public ByteBuffer asByteBuffer() {
    ByteBuffer cloned = byteBuffer.asReadOnlyBuffer();
    cloned.position(0);
    return cloned;
  }

  @Override
  public void release() {
    dispatcher.recycleBuffer(this);
  }
}
