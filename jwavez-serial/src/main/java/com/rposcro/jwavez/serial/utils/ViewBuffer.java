package com.rposcro.jwavez.serial.utils;

import java.nio.ByteBuffer;

public class ViewBuffer {

  private ByteBuffer buffer;

  private int offset;
  private int length;
  private int position;

  public ViewBuffer(ByteBuffer buffer) {
    this.buffer = buffer;
    this.offset = buffer.position();
    this.position = 0;
    this.length = 0;
  }

  public void setViewRange(int offset, int length) {
    this.offset = offset;
    this.length = length;
    this.position = 0;
  }

  public byte get() {
    return buffer.get(offset + checkIndex(position++));
  }

  public byte get(int index) {
    return buffer.get(offset + checkIndex(index));
  }

  public boolean hasRemaining() {
    return position < length;
  }

  public int remaining() {
    return length - position;
  }

  public int length() {
    return length;
  }

  public int position() {
    return position;
  }

  private int checkIndex(int index) {
    if ((index < 0) || (index >= length))
      throw new IndexOutOfBoundsException();
    return index;
  }
}
