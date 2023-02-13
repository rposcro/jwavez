package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.model.BitLength;

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

    public static String asString(byte[] buffer) {
        return IntStream.range(0, buffer.length)
                .mapToObj(idx -> format("%02x", buffer[idx]))
                .collect(Collectors.joining(" "));
    }

    public static String asConciseString(byte[] buffer) {
        return IntStream.range(0, buffer.length)
                .mapToObj(idx -> format("%02x", buffer[idx]))
                .collect(Collectors.joining(""));
    }

    public static byte[] asByteArray(int[] values) {
        byte[] bytes = new byte[values.length];
        for (int idx = 0; idx < values.length; idx++) {
            bytes[idx] = (byte) values[idx];
        }
        return bytes;
    }

    public static byte[] asByteArray(String encoded) {
        encoded = encoded.replace(" ", "");
        encoded = encoded.length() % 2 == 0 ? encoded : "0" + encoded;
        byte[] bytes = new byte[encoded.length() / 2];
        for (int idx = 0; idx < bytes.length; idx++) {
            bytes[idx] = (byte) Integer.parseInt(encoded.substring(idx * 2, idx * 2 + 2), 16);
        }
        return bytes;
    }
}
