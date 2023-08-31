package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableBufferTest {

    @Test
    public void throwsExceptionWhenGetOutsideSize() {
        ImmutableBuffer buffer = new ImmutableByteBuffer(
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
        ImmutableBuffer buffer = new ImmutableByteBuffer(
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
        ImmutableByteBuffer buffer = new ImmutableByteBuffer(
                new byte[] { 0x10, 0x20, 0x30, 0x40 }, 0, 4);
        buffer.invalidate();

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getByte(0));
    }

    @Test
    public void getsValue() {
        ImmutableByteBuffer buffer = new ImmutableByteBuffer(
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
        ImmutableByteBuffer buffer = new ImmutableByteBuffer(
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

    @Test
    public void positionsBuffer() {
        ImmutableByteBuffer buffer = new ImmutableByteBuffer(
                new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 }, 0);

        assertEquals(0x0010, buffer.nextUnsignedWord());
        assertEquals(0x2030, buffer.nextUnsignedWord());
        buffer.position(2);
        assertEquals(0x2030, buffer.nextUnsignedWord());
    }

    @Test
    public void skipsBytes() {
        ImmutableByteBuffer buffer = new ImmutableByteBuffer(
                new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 }, 0);

        assertEquals(0x0010, buffer.nextUnsignedWord());
        buffer.skip(2);
        assertEquals(0x4070, buffer.nextUnsignedWord());
    }

    @Test
    public void clonesBytes() {
        byte[] data = new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 };
        ImmutableBuffer buffer = new ImmutableByteBuffer(data, 0);

        byte[] cloned = buffer.cloneBytes();
        assertArrayEquals(data, cloned);
    }

    @Test
    public void clonesBytesWithLength() {
        byte[] data = new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 };
        ImmutableBuffer buffer = new ImmutableByteBuffer(data, 0);

        byte[] expected = new byte[] { 0x00, 0x10, 0x20 };
        byte[] cloned = buffer.cloneBytes(3);
        assertArrayEquals(expected, cloned);
    }

    @Test
    public void clonesRemainingBytes() {
        byte[] data = new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 };
        ImmutableBuffer buffer = new ImmutableByteBuffer(data, 0);
        buffer.position(3);

        byte[] expected = new byte[] { 0x30, 0x40, 0x70 };
        byte[] cloned = buffer.cloneRemainingBytes();
        assertArrayEquals(expected, cloned);
    }

    @Test
    public void clonesRemainingBytesWithLength() {
        byte[] data = new byte[] { 0x00, 0x10, 0x20, 0x30, 0x40, 0x70 };
        ImmutableBuffer buffer = new ImmutableByteBuffer(data, 0);
        buffer.position(3);

        byte[] expected = new byte[] { 0x30, 0x40 };
        byte[] cloned = buffer.cloneRemainingBytes(2);
        assertArrayEquals(expected, cloned);
    }

    @Test
    public void copiesBytes() {
        byte[] data = new byte[] { 0x00, 0x10, 0x20 };
        ImmutableBuffer buffer = new ImmutableByteBuffer(data, 0);

        byte[] dest = new byte[5];
        byte[] expected = new byte[] { 0x00, 0x00, 0x10, 0x20, 0x00 };
        buffer.copyBytes(dest, 1);
        assertArrayEquals(expected, dest);
    }
}
