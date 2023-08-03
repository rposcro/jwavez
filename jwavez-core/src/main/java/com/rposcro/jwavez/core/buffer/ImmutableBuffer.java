package com.rposcro.jwavez.core.buffer;

/**
 * Immutable temporary byte buffer, this is one time usage buffer for read only purposes.
 * Routines which receive and operate on instances of the class must not store any references
 * as they are likely to be invalidated when the routine call is completed.
 * Access to the underlying byte data is not synchronized, so concurrent usage of nextXXX(...)
 * methods needs to be considered.
 *
 * All methods provided refer to virtual position and length.
 */
public interface ImmutableBuffer {

    int position();
    int length();
    int available();

    ImmutableBuffer position(int position);
    ImmutableBuffer skip(int distance);
    ImmutableBuffer rewind();

    byte getByte(int index);
    short getWord(int index);
    int getDoubleWord(int index);
    short getUnsignedByte(int index);
    int getUnsignedWord(int index);
    long getUnsignedDoubleWord(int index);

    boolean hasNext();
    byte next();
    byte nextByte();
    short nextWord();
    int nextDoubleWord();
    short nextUnsignedByte();
    int nextUnsignedWord();
    long nextUnsignedDoubleWord();

    byte[] cloneBytes();
    byte[] cloneBytes(int length);
    byte[] cloneRemainingBytes();
    byte[] cloneRemainingBytes(int length);
    void copyBytes(byte[] toArray, int toOffset);

    void dispose();

    static ImmutableBuffer overBuffer(byte[] buffer) {
        return overBuffer(buffer, 0, buffer.length);
    }

    static ImmutableBuffer overBuffer(byte[] buffer, int payloadOffset, int payloadLength) {
        byte assertByte = buffer[payloadLength + payloadOffset - 1];
        assertByte = buffer[payloadOffset];
        return new ImmutableByteBuffer(buffer, payloadOffset, payloadLength);
    }

    static ImmutableBuffer empty() {
        return new ImmutableByteBuffer(null, 0, 0);
    }
}
