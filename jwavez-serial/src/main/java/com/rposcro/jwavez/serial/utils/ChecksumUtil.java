package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.core.buffer.ByteBuffer;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

public class ChecksumUtil {

    public static byte crc(ByteBuffer buffer, int offset, int length) {
        byte crc = (byte) 0xff;
        for (int idx = offset; idx < offset + length; idx++) {
            crc ^= buffer.get(idx);
        }
        return crc;
    }

    public static byte frameCrc(byte[] frameBuffer) {
        byte crc = (byte) 0xff;
        for (int idx = 1; idx < frameBuffer.length - 1; idx++) {
            crc ^= frameBuffer[idx];
        }
        return crc;
    }

    public static byte frameCrc(ImmutableBuffer buffer) {
        byte crc = (byte) 0xff;
        for (int idx = 1; idx < buffer.length() - 1; idx++) {
            crc ^= buffer.getByte(idx);
        }
        return crc;
    }
}
