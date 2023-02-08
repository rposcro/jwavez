package com.rposcro.jwavez.serial.buffers.dispatchers;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.MAX_Z_WAVE_FRAME_SIZE;

import com.rposcro.jwavez.serial.buffers.DirectFrameBuffer;
import com.rposcro.jwavez.serial.exceptions.FatalSerialException;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import lombok.Builder;

public class SingletonBufferDispatcher implements BufferDispatcher<DirectFrameBuffer> {

    private final Semaphore bufferLock;
    private final ByteBuffer byteBuffer;
    private final DirectFrameBuffer singletonBuffer;

    @Builder
    public SingletonBufferDispatcher() {
        this.byteBuffer = ByteBuffer.allocateDirect(MAX_Z_WAVE_FRAME_SIZE);
        this.singletonBuffer = DirectFrameBuffer.builder()
                .byteBuffer(byteBuffer)
                .releaseListener(this::recycleBuffer)
                .build();
        this.bufferLock = new Semaphore(1);
    }

    @Override
    public DirectFrameBuffer allocateBuffer(int size) {
        if (size > MAX_Z_WAVE_FRAME_SIZE) {
            throw new FatalSerialException("Cannot allocate buffer larger than " + MAX_Z_WAVE_FRAME_SIZE);
        }

        bufferLock.acquireUninterruptibly();
        byteBuffer.position(0);
        byteBuffer.limit(size);
        return singletonBuffer;
    }

    private void recycleBuffer(DirectFrameBuffer frameBuffer) {
        if (frameBuffer != this.singletonBuffer) {
            throw new FatalSerialException("Non-owned buffer cannot be recycled here!");
        }
        bufferLock.release();
    }
}
