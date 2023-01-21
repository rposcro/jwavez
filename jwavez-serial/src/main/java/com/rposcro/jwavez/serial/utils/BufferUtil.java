package com.rposcro.jwavez.serial.utils;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;

import java.util.stream.IntStream;

public class BufferUtil {

    public static String bufferToString(ViewBuffer buffer) {
        StringBuffer string = new StringBuffer();
        IntStream.range(0, buffer.length())
                .forEach(index -> string.append(String.format("%02x ", buffer.get(index))));
        return string.toString();
    }

    public static String bufferToString(byte[] buffer) {
        StringBuffer string = new StringBuffer();
        for (byte bt : buffer) {
            string.append(String.format("%02x ", bt));
        }
        return string.toString();
    }
}
