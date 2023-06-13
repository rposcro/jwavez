package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferTest {

    @Test
    public void buildsBufferData() {
        ByteBuffer buffer = new ByteBuffer(100, null);
        assertEquals(0, buffer.getLength());

        buffer.add((byte) 0x50);
        buffer.add((byte) 0x40);
        assertEquals(2, buffer.getLength());
        assertEquals(0x50, buffer.get(0));
        assertEquals(0x40, buffer.get(1));
    }
}
