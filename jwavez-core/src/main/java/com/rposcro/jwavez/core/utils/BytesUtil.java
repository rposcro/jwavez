package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.model.BitLength;

public class BytesUtil {

    public static byte extractValue(byte value, int startBit, int mask) {
        return (byte) ((value >> startBit) & (mask));
    }

    public static void writeMSBValue(byte[] buffer, int offset, BitLength valueSize, int value) {
        for (int idx = valueSize.getBytesNumber() - 1; idx >= 0; idx--) {
            buffer[offset + idx] = (byte) value;
            value >>= 8;
        }
    }
}
