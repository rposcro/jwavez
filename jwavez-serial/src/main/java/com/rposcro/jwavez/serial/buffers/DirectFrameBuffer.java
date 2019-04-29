package com.rposcro.jwavez.serial.buffers;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.NonNull;

@Builder
public final class DirectFrameBuffer implements FrameBuffer {

  private ByteBuffer byteBuffer;
  private Consumer<DirectFrameBuffer> releaseListener;

  DirectFrameBuffer(@NonNull ByteBuffer byteBuffer, @NonNull Consumer<DirectFrameBuffer> releaseListener) {
    this.releaseListener = releaseListener;
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
    releaseListener.accept(this);
  }
}
