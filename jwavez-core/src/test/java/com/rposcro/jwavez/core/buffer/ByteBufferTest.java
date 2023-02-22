package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ByteBufferTest {

    @Test
    public void buildsBufferData() {
        ByteBuffer buffer = new ByteBuffer(100);
        assertEquals(0, buffer.getLength());

        buffer.add((byte) 0x50);
        buffer.add((byte) 0x40);
        assertEquals(2, buffer.getLength());
        assertEquals(0x50, buffer.get(0));
        assertEquals(0x40, buffer.get(1));
    }

    @Test
    public void returnsValidImmutableBuffer() {
        ByteBuffer buffer = new ByteBuffer(100);
        buffer.add((byte) 0x50);
        buffer.add((byte) 0x40);
        buffer.add((byte) 0x30);
        buffer.add((byte) 0x20);
        ImmutableBuffer immutable = buffer.toImmutableBuffer();

        assertEquals(0, immutable.position());
        assertEquals(4, immutable.length());
        assertEquals(4, immutable.available());
        assertEquals(0x50403020, immutable.nextDoubleWord());
    }

    @Test
    public void invalidatesImmutableBufferAndCreatesAnother() {
        ByteBuffer buffer = new ByteBuffer(100);
        buffer.add((byte) 0x50);
        buffer.add((byte) 0x40);
        ImmutableBuffer immutableBefore = buffer.toImmutableBuffer();

        buffer.add((byte) 0x60);
        ImmutableBuffer immutableAfter = buffer.toImmutableBuffer();

        assertEquals(0, immutableBefore.length());
        assertEquals(0, immutableBefore.available());
        assertEquals(0, immutableBefore.position());
        assertThrows(IndexOutOfBoundsException.class, () -> immutableBefore.getByte(0));

        assertEquals(3, immutableAfter.length());
        assertEquals(3, immutableAfter.available());
        assertEquals(0, immutableAfter.position());
        assertEquals(0x4060, immutableAfter.skip(1).nextWord());
    }

    @Test
    public void clearsBuffer() {
        ByteBuffer buffer = new ByteBuffer(100);
        buffer.add((byte) 0x50);
        buffer.add((byte) 0x40);
        ImmutableBuffer immutableBefore = buffer.toImmutableBuffer();

        buffer.clear();
        buffer.add((byte) 0x30);
        ImmutableBuffer immutableAfter = buffer.toImmutableBuffer();

        assertEquals(0, immutableBefore.length());
        assertEquals(0, immutableBefore.available());
        assertEquals(0, immutableBefore.position());
        assertThrows(IndexOutOfBoundsException.class, () -> immutableBefore.getByte(0));

        assertEquals(1, immutableAfter.length());
        assertEquals(1, immutableAfter.available());
        assertEquals(0, immutableAfter.position());
        assertEquals(0x30, immutableAfter.nextByte());
    }
}
