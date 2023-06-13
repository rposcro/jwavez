package com.rposcro.jwavez.core.buffer;

import lombok.AccessLevel;
import lombok.Getter;

public final class ByteBuffer {

    @Getter(AccessLevel.MODULE)
    private byte[] data;

    @Getter
    private ByteBufferManager bufferManager;

    @Getter
    private int length;

    public ByteBuffer(int size, ByteBufferManager bufferManager) {
        this.data = new byte[size];
        this.length = 0;
        this.bufferManager = bufferManager;
    }

    public ByteBuffer clear() {
        this.length = 0;
        return this;
    }

    public ByteBuffer add(byte value) {
        data[length++] = value;
        return this;
    }

    public byte get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void dispose() {
        if (bufferManager != null) {
            bufferManager.releaseBuffer(this);
        }
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
        }
    }
}
