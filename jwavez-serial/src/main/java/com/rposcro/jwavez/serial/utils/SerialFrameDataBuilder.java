package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.buffer.ImmutableBufferBuilder;

public class SerialFrameDataBuilder extends ImmutableBufferBuilder {

    public SerialFrameDataBuilder(ByteBufferManager byteBufferManager, int dataSize) {
        super(byteBufferManager,  dataSize);
    }

    @Override
    public ImmutableBuffer build() {
        add(ChecksumUtil.crc(byteBuffer, 1, byteBuffer.getLength() - 1));
        return super.build();
    }
}
