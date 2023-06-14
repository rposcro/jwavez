package com.rposcro.jwavez.core.buffer;

/**
 * Properties:<br>
 * <li><b>data</b> physical byte array where this buffer is built on</li>
 * <li><b>offset</b> offset in physical byte array where this buffer starts from</li>
 * <li><b>length</b> length of this virtual buffer, it's not same as physical byte array length</li>
 * <li><b>position</b> current index position in this virtual buffer, physical array position is offset + position</li>
 */
final class ImmutableByteBuffer implements ImmutableBuffer {

    private int offset;
    private int length;
    private int position;
    private byte[] data;
    private ByteBuffer byteBuffer;

    ImmutableByteBuffer(ByteBuffer byteBuffer, int length) {
        this.byteBuffer = byteBuffer;
        this.data = byteBuffer.getData();
        this.length = length;
    }

    ImmutableByteBuffer(byte[] buffer, int offset) {
        this(buffer, offset, buffer.length);
    }

    ImmutableByteBuffer(byte[] buffer, int offset, int length) {
        this.data = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;
        this.byteBuffer = null;
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public int available() {
        return length - position;
    }

    @Override
    public boolean hasNext() {
        return position < length;
    }

    @Override
    public ImmutableByteBuffer position(int position) {
        this.position = position;
        return this;
    }

    @Override
    public ImmutableByteBuffer skip(int distance) {
        position += distance;
        return this;
    }

    @Override
    public ImmutableByteBuffer rewind() {
        position = 0;
        return this;
    }

    @Override
    public byte getByte(int index) {
        checkIndex(index);
        return data[offset + index];
    }

    @Override
    public short getWord(int index) {
        return (short) getUnsignedWord(index);
    }

    @Override
    public int getDoubleWord(int index) {
        return (int) getUnsignedDoubleWord(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        checkIndex(index);
        return (short) (data[offset + index] & 0xFF);
    }

    @Override
    public int getUnsignedWord(int index) {
        checkIndex(index + 1);
        return ((data[offset + index] & 0xFF) << 8) | (data[offset + index + 1] & 0xFF);
    }

    @Override
    public long getUnsignedDoubleWord(int index) {
        checkIndex(index + 3);
        long value = ((long) (data[offset + index] & 0xFF)) << 24;
        value |= ((long) (data[offset + index + 1] & 0xFF)) << 16;
        value |= ((long) (data[offset + index + 2] & 0xFF)) << 8;
        value |= ((long) (data[offset + index + 3] & 0xFF));
        return value;
    }

    @Override
    public byte next() {
        checkIndex(position);
        return data[offset + (position++)];
    }

    @Override
    public byte nextByte() {
        return next();
    }

    @Override
    public short nextWord() {
        short value = getWord(position);
        position += 2;
        return value;
    }

    @Override
    public int nextDoubleWord() {
        int value = getDoubleWord(position);
        position += 4;
        return value;
    }

    @Override
    public short nextUnsignedByte() {
        short value = getUnsignedByte(position);
        position++;
        return value;
    }

    @Override
    public int nextUnsignedWord() {
        int value = getUnsignedWord(position);
        position += 2;
        return value;
    }

    @Override
    public long nextUnsignedDoubleWord() {
        long value = getUnsignedDoubleWord(position);
        position += 4;
        return value;
    }

    @Override
    public byte[] cloneBytes() {
        byte[] cloned = new byte[length];
        System.arraycopy(data, offset, cloned, 0, length);
        return cloned;
    }

    @Override
    public byte[] cloneBytes(int length) {
        byte[] cloned = new byte[length];
        System.arraycopy(data, offset, cloned, 0, length);
        return cloned;
    }

    @Override
    public byte[] cloneRemainingBytes() {
        int remaining = length - position;
        byte[] cloned = new byte[remaining];
        System.arraycopy(data, offset + position, cloned, 0, remaining);
        return cloned;
    }

    @Override
    public void cloneBytes(byte[] toArray, int toOffset) {
        System.arraycopy(data, offset, toArray, toOffset, length);
    }

    @Override
    public void dispose() {
        if (byteBuffer != null) {
            byteBuffer.dispose();
            byteBuffer = null;
        }
        invalidate();
    }

    void invalidate() {
        data = null;
        position = 0;
        length = 0;
        offset = 0;
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.format("Index %s is out of payload length %s!", index, length));
        }
    }
}
