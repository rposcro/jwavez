package com.rposcro.jwavez.core.buffer;

import lombok.Getter;

/**
 * Immutable temporary byte buffer, this is one time usage buffer for read only purposes.
 * Routines which receive and operate on instances of the class must not store any references
 * as they are likely to be invalidated when the routine call is completed.
 * Access to the underlying byte data is not synchronized, so concurrent usage of nextXXX(...)
 * methods needs to be considered.
 *
 * All methods provided refer to virtual position and length.
 * <p>
 * Properties:<br>
 * <li><b>data</b> physical byte array where this buffer is built on</li>
 * <li><b>offset</b> offset in physical byte array where this buffer starts from</li>
 * <li><b>length</b> length of this virtual buffer, it's not same as physical byte array length</li>
 * <li><b>position</b> current index position in this virtual buffer, physical array position is offset + position</li>
 */
public final class ImmutableBuffer {

    private int offset;
    private int length;
    private int position;
    private byte[] data;

    ImmutableBuffer(byte[] buffer, int offset) {
        this(buffer, offset, buffer.length);
    }

    ImmutableBuffer(byte[] buffer, int offset, int length) {
        this.data = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;
    }

    public int position() {
        return position;
    }

    public int length() {
        return length;
    }

    public int available() {
        return length - position;
    }

    public boolean hasNext() {
        return position < length;
    }

    public ImmutableBuffer skip(int distance) {
        position += distance;
        return this;
    }

    public ImmutableBuffer rewind() {
        position = 0;
        return this;
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
        long value = ((long) (data[offset + index] & 0xFF)) << 24;
        value |= ((long) (data[offset + index + 1] & 0xFF)) << 16;
        value |= ((long) (data[offset + index + 2] & 0xFF)) << 8;
        value |= ((long) (data[offset + index + 3] & 0xFF));
        return value;
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

    public long nextUnsignedDoubleWord() {
        long value = getUnsignedDoubleWord(position);
        position += 4;
        return value;
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

    void invalidate() {
        data = null;
        position = 0;
        length = 0;
        offset = 0;
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
        }
    }

    public static ImmutableBuffer overBuffer(byte[] buffer) {
        return overBuffer(buffer, 0, buffer.length);
    }

    public static ImmutableBuffer overBuffer(byte[] buffer, int payloadOffset, int payloadLength) {
        byte assertByte = buffer[payloadLength + payloadOffset - 1];
        assertByte = buffer[payloadOffset];
        return new ImmutableBuffer(buffer, payloadOffset, payloadLength);
    }
}
