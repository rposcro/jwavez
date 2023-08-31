package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ByteBufferManagerTest {

    @Test
    public void cachedBuffersRunOut() {
        ByteBufferManager manager = new ByteBufferManager(1);
        ByteBuffer bufferCached = manager.obtainBuffer();
        ByteBuffer bufferUnmanaged = manager.obtainBuffer();
        manager.releaseBuffer(bufferCached);

        assertNotEquals(bufferUnmanaged, bufferCached);
        assertNotEquals(bufferUnmanaged, manager.obtainBuffer());
    }

    @Test
    public void cachedBuffersAreRecycled() {
        ByteBufferManager manager = new ByteBufferManager(2);
        ByteBuffer buffer1 = manager.obtainBuffer();
        ByteBuffer buffer2 = manager.obtainBuffer();

        assertNotNull(buffer1);
        assertNotNull(buffer2);

        manager.releaseBuffer(buffer1);
        assertEquals(buffer1, manager.obtainBuffer());
    }
}
