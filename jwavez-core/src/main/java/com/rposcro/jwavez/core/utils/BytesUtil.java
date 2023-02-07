package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.constants.BitLength;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class BytesUtil {

    public static void writeMSBValue(byte[] buffer, int offset, BitLength valueSize, int value) {
        for (int idx = valueSize.getBytesNumber() - 1; idx >= 0; idx--) {
            buffer[offset + idx] = (byte) value;
            value >>= 8;
        }
    }

    public static String arrayToString(byte[] buffer) {
        return IntStream.range(0, buffer.length)
                .mapToObj(idx -> format("%02x", buffer[idx]))
                .collect(Collectors.joining(" "));
    }

    public static byte[] toByteArray(int[] values) {
        byte[] bytes = new byte[values.length];
        for (int idx = 0; idx < values.length; idx++) {
            bytes[idx] = (byte) values[idx];
        }
        return bytes;
    }
}
