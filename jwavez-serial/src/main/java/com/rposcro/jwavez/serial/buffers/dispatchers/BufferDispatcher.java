package com.rposcro.jwavez.serial.buffers.dispatchers;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;

public interface BufferDispatcher<T extends FrameBuffer> {

    T allocateBuffer(int size);
}
