package com.rposcro.jwavez.core.utils;

import lombok.Getter;

/**
 * All methods provided refer to virtual position and length
 *
 * Properties:<br>
 *     <li><b>data</b> physical byte array where this buffer is built on</li>
 *     <li><b>offset</b> offset in physical byte array where this buffer starts from</li>
 *     <li><b>length</b> length of this virtual buffer, it's not same as physical byte array length</li>
 *     <li><b>position</b> current index position in this virtual buffer, physical array position is offset + position</li>
 */
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
    return (int) getUnsignedDoubleWord(index);
  }

  public short getUnsignedByte(int index) {
    checkIndex(index);
    return (short) (data[offset + index] & 0xFF);
  }

  public int getUnsignedWord(int index) {
    checkIndex(index + 1);
    return ((data[offset + index] & 0xFF) << 8) | (data[offset + index + 1] & 0xFF);
  }

  public long getUnsignedDoubleWord(int index) {
    checkIndex(index + 3);
    long value = ((long)(data[offset + index] & 0xFF)) << 24;
    value |= ((long)(data[offset + index + 1] & 0xFF)) << 16;
    value |= ((long)(data[offset + index + 2] & 0xFF)) << 8;
    value |= ((long)(data[offset + index + 3] & 0xFF));
    return value;
  }

  public int available() {
    return length - position;
  }

  public boolean hasNext() {
    return position < length;
  }

  public byte next() {
    checkIndex(position);
    return data[offset + (position++)];
  }

  public byte nextByte() {
    return next();
  }

  public short nextWord() {
    short value = getWord(position);
    position += 2;
    return value;
  }

  public int nextDoubleWord() {
    int value = getDoubleWord(position);
    position += 4;
    return value;
  }

  public short nextUnsignedByte() {
    short value = getUnsignedByte(position);
    position++;
    return value;
  }

  public int nextUnsignedWord() {
    int value = getUnsignedWord(position);
    position += 2;
    return value;
  }

  public ImmutableBuffer skip(int distance) {
    if (position + distance >= length) {
      throw new IndexOutOfBoundsException("New index out of bound");
    }
    position += distance;
    return this;
  }

  public ImmutableBuffer rewind() {
    position = 0;
    return this;
  }

  public byte[] cloneBytes() {
    byte[] cloned = new byte[length];
    System.arraycopy(data, offset, cloned, 0, length);
    return cloned;
  }

  public byte[] cloneRemainingBytes() {
    int remaining = length - position;
    byte[] cloned = new byte[remaining];
    System.arraycopy(data, offset + position, cloned, 0, remaining);
    return cloned;
  }

  public void cloneBytes(byte[] toArray, int toOffset) {
    System.arraycopy(data, offset, toArray, toOffset, length);
  }

  public static ImmutableBuffer overBuffer(byte[] buffer) {
    return overBuffer(buffer, 0, buffer.length);
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
