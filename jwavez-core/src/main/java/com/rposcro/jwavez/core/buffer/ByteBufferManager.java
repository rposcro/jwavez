package com.rposcro.jwavez.core.buffer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ByteBufferManager {

    private final static int MAX_BUFFER_SIZE = 256;
    private final static int DEFAULT_BUFFERS_NUMBER = 10;

    private final LinkedBlockingQueue<ByteBuffer> availableBuffers;
    private final Set<ByteBuffer> occupiedBuffers;

    public ByteBufferManager() {
        this(DEFAULT_BUFFERS_NUMBER);
    }

    public ByteBufferManager(int buffersNumber) {
        this.availableBuffers = new LinkedBlockingQueue<>(buffersNumber);
        this.occupiedBuffers = new HashSet<>(buffersNumber);

        for (int i = 0; i < buffersNumber; i++) {
            availableBuffers.offer(new ByteBuffer(MAX_BUFFER_SIZE));
        }
    }

    public ByteBuffer obtainBuffer() {
        return obtainBuffer(MAX_BUFFER_SIZE);
    }

    public ByteBuffer obtainBuffer(int minCapacity) {
        ByteBuffer buffer = availableBuffers.poll();
        if (buffer == null) {
            buffer = new ByteBuffer(minCapacity);
        } else {
            occupiedBuffers.add(buffer);
        }
        return buffer;
    }

    public void releaseBuffer(ByteBuffer buffer) {
        if (this.occupiedBuffers.remove(buffer)) {
            this.availableBuffers.offer(buffer);
        }
    }
}
