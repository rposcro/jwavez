package com.rposcro.jwavez.core.utils;

import lombok.Getter;

public final class ImmutableBuffer {

  private byte[] data;
  @Getter
  private int offset;
  @Getter
  private int length;

  private int position;

  private ImmutableBuffer(byte[] buffer, int offset, int length) {
    this.data = buffer;
    this.offset = offset;
    this.length = length;
    this.position = 0;
  }

  public byte getByte(int index) {
    checkIndex(index);
    return data[offset + index];
  }

  public short getWord(int index) {
    return (short) getUnsignedWord(index);
  }

  public int getDoubleWord(int index) {
    checkIndex(index + 3);
    long value = ((long)(data[offset + index] & 0xFF)) << 24;
    value |= ((long)(data[offset + index + 1] & 0xFF)) << 16;
    value |= ((long)(data[offset + index + 2] & 0xFF)) << 8;
    value |= ((long)(data[offset + index + 3] & 0xFF));
    return (int) value;
  }

  public short getUnsignedByte(int index) {
    checkIndex(index);
    return (short) (data[offset + index] & 0xFF);
  }

  public int getUnsignedWord(int index) {
    checkIndex(index + 1);
    return ((data[offset + index] & 0xFF) << 8) | (data[offset + index + 1] & 0xFF);
  }

  public byte[] cloneBytes() {
    byte[] cloned = new byte[length];
    System.arraycopy(data, offset, cloned, 0, length);
    return cloned;
  }

  public boolean hasNext() {
    return position < length;
  }

  public byte next() {
    if (position >= length) {
      throw new IndexOutOfBoundsException(String.format("No more data available"));
    }
    return data[offset + (position++)];
  }

  public ImmutableBuffer rewind() {
    position = 0;
    return this;
  }

  public static ImmutableBuffer overBuffer(byte[] buffer, int payloadOffset, int payloadLength) {
    byte assertByte = buffer[payloadLength + payloadOffset - 1];
    assertByte = buffer[payloadOffset];
    return new ImmutableBuffer(buffer, payloadOffset, payloadLength);
  }

  private void checkIndex(int index) {
    if (index >= length) {
      throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
    }
  }
}
