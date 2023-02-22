package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableBufferTest {

    @Test
    public void throwsExceptionWhenGetOutsideSize() {
        ImmutableBuffer buffer = new ImmutableBuffer(
                new byte[] { 0x10, 0x20, 0x30, 0x40 }, 0, 4);

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getByte(4));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getUnsignedByte(4));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getWord(3));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getUnsignedWord(3));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getDoubleWord(2));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getUnsignedDoubleWord(1));
    }

    @Test
    public void throwsExceptionWhenNoMoreDate() {
        ImmutableBuffer buffer = new ImmutableBuffer(
                new byte[] { 0x10, 0x20, 0x30, 0x40 }, 0, 4);
        buffer.skip(4);

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.next());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextByte());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextUnsignedByte());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextWord());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextUnsignedWord());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextDoubleWord());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.nextUnsignedDoubleWord());
    }

    @Test
    public void throwsExceptionWhenInvalidated() {
        ImmutableBuffer buffer = new ImmutableBuffer(
                new byte[] { 0x10, 0x20, 0x30, 0x40 }, 0, 4);
        buffer.invalidate();

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getByte(0));
    }

    @Test
    public void getsValue() {
        ImmutableBuffer buffer = new ImmutableBuffer(
                new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70, 0x55 }, 0, 5);

        assertEquals(0x20, buffer.getByte(2));
        assertEquals(0x30, buffer.getUnsignedByte(3));
        assertEquals(0x3040, buffer.getWord(3));
        assertEquals(0x0010, buffer.getUnsignedWord(0));
        assertEquals(0x10203040, buffer.getDoubleWord(1));
        assertEquals(0x00102030, buffer.getUnsignedDoubleWord(0));
    }

    @Test
    public void returnsNextValues() {
        ImmutableBuffer buffer = new ImmutableBuffer(
                new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70, 0x55, 0x00 }, 0);

        assertEquals(0x00, buffer.next());
        assertEquals(0x10, buffer.nextByte());
        assertEquals(0x20, buffer.nextUnsignedByte());
        buffer.rewind();
        assertEquals(0x0010, buffer.nextWord());
        assertEquals(0x2030, buffer.nextUnsignedWord());
        buffer.rewind();
        assertEquals(0x00102030, buffer.nextDoubleWord());
        assertEquals(0x40705500, buffer.nextUnsignedDoubleWord());
    }
}
