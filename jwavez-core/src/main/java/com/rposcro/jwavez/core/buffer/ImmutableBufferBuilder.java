package com.rposcro.jwavez.core.buffer;

import java.util.function.Supplier;

public class ImmutableBufferBuilder {

    protected final int bufferSize;
    protected ByteBuffer byteBuffer;

    public ImmutableBufferBuilder(ByteBufferManager byteBufferManager, int bufferSize) {
        this.byteBuffer = byteBufferManager.obtainBuffer(bufferSize);
        this.bufferSize = bufferSize;
    }

    public ImmutableBufferBuilder add(byte value) {
        byteBuffer.add(value);
        return this;
    }

    public ImmutableBufferBuilder add(Supplier<Byte> valueSupplier) {
        byteBuffer.add(valueSupplier.get());
        return this;
    }

    public ImmutableBuffer build() {
        ImmutableBuffer immutableBuffer = new ImmutableByteBuffer(byteBuffer, bufferSize);
        this.byteBuffer = null;
        return immutableBuffer;
    }
}
