package com.rposcro.jwavez.serial.utils;

import lombok.Getter;

@Getter
public class ByteBuffer {

  private byte[] buffer;
  private int capacity;
  private int length;
  private int position;

  public ByteBuffer(int capacity) {
    this.buffer = new byte[capacity];
    this.capacity = capacity;
    this.length = 0;
    this.position = 0;
  }

  public void put(byte chunk) {
    buffer[length++] = chunk;
  }

  public byte get(int idx) {
    if (idx >= length) {
      throw new IndexOutOfBoundsException("No more bytes in this buffer!");
    }
    return buffer[idx];
  }

  public byte get() {
    if (position >= length) {
      throw new IndexOutOfBoundsException("No more bytes in this buffer!");
    }
    return buffer[position++];
  }

  public void clear() {
    length = 0;
    position = 0;
  }

  public void rewind() {
    position = 0;
  }

  public int getPosition() {
    return this.position;
  }

  public void setPosition(int position) {
    if (position >= length) {
      throw new IndexOutOfBoundsException(String.format("Position %s is greater than length %s", position, this.length));
    }
    this.position = position;
  }

  public byte[] cloneArray() {
    byte[] array = new byte[length];
    System.arraycopy(buffer, 0, array, 0, length);
    return array;
  }
}
