package com.rposcro.jwavez.core.buffer;

import lombok.Getter;

public final class ByteBuffer {

    private byte[] data;
    private ImmutableBuffer immutableBuffer;

    @Getter
    private int length;

    public ByteBuffer(int size) {
        this.data = new byte[size];
        this.length = 0;
    }

    public ByteBuffer clear() {
        this.length = 0;
        invalidateImmutableBuffer();
        return this;
    }

    public void add(byte value) {
        data[length++] = value;
        invalidateImmutableBuffer();
    }

    public byte get(int index) {
        checkIndex(index);
        return data[index];
    }

    public ImmutableBuffer toImmutableBuffer() {
        if (immutableBuffer == null) {
            immutableBuffer = new ImmutableBuffer(data, 0, length);
        }
        return immutableBuffer;
    }

    private void invalidateImmutableBuffer() {
        if (immutableBuffer != null) {
            immutableBuffer.invalidate();
            immutableBuffer = null;
        }
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
        }
    }
}
