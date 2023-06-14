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
        add(FramesUtil.frameCRC(byteBuffer));
        return super.build();
    }
}
