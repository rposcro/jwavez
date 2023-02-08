package com.rposcro.jwavez.serial.buffers;

import static java.lang.Integer.toUnsignedLong;

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
        this.length = buffer.limit();
    }

    protected void setViewRange(int offset, int length) {
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

    public int getUnsignedWord() {
        return ((get() & 0xff) << 8) | (get() & 0xFF);
    }

    public long getUnsignedDWord() {
        return toUnsignedLong(((get() & 0xff) << 24) | ((get() & 0xff) << 16) | ((get() & 0xff) << 8) | (get() & 0xff));
    }

    public byte[] getBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = get();
        }
        return bytes;
    }

    public byte[] copyBytes() {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = get(i);
        }
        return bytes;
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

    public ViewBuffer position(int position) {
        if (position >= length) {
            throw new IndexOutOfBoundsException("Position " + position + " out of allowed length " + length);
        }
        this.position = position;
        return this;
    }

    private int checkIndex(int index) {
        if ((index < 0) || (index >= length)) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }
}
