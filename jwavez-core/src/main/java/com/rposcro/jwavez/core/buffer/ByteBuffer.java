package com.rposcro.jwavez.core.buffer;

import lombok.Getter;

public final class ByteBuffer {

    private byte[] data;
    private int position;
    @Getter
    private int length;

    public ByteBuffer(int size) {
        this.data = new byte[size];
        this.length = 0;
        this.position = 0;
    }

    public int available() {
        return length - position;
    }

    public ByteBuffer rewind() {
        position = 0;
        return this;
    }

    public void clear() {
        this.position = 0;
        this.length = 0;
    }

    public boolean hasNext() {
        return position < length;
    }

    public byte getByte(int index) {
        checkIndex(index);
        return data[index];
    }

    public short getWord(int index) {
        return (short) getUnsignedWord(index);
    }

    public int getDoubleWord(int index) {
        return (int) getUnsignedDoubleWord(index);
    }

    public short getUnsignedByte(int index) {
        checkIndex(index);
        return (short) (data[index] & 0xFF);
    }

    public int getUnsignedWord(int index) {
        checkIndex(index + 1);
        return ((data[index] & 0xFF) << 8) | (data[index + 1] & 0xFF);
    }

    public long getUnsignedDoubleWord(int index) {
        checkIndex(index + 3);
        long value = ((long) (data[index] & 0xFF)) << 24;
        value |= ((long) (data[index + 1] & 0xFF)) << 16;
        value |= ((long) (data[index + 2] & 0xFF)) << 8;
        value |= ((long) (data[index + 3] & 0xFF));
        return value;
    }

    public byte next() {
        checkIndex(position);
        return data[(position++)];
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

    public ByteBuffer skip(int distance) {
        if (position + distance >= length) {
            throw new IndexOutOfBoundsException("New index out of bound");
        }
        position += distance;
        return this;
    }

    public void push(byte value) {
        data[length++] = value;
    }

    public byte[] cloneBytes() {
        byte[] cloned = new byte[length];
        System.arraycopy(data, 0, cloned, 0, length);
        return cloned;
    }

    public byte[] cloneRemainingBytes() {
        int remaining = length - position;
        byte[] cloned = new byte[remaining];
        System.arraycopy(data, position, cloned, 0, remaining);
        return cloned;
    }

    public void cloneBytes(byte[] toArray, int toOffset) {
        System.arraycopy(data, 0, toArray, toOffset, length);
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
        }
    }
}
