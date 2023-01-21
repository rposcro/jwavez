package com.rposcro.jwavez.core.utils;

public class BitsUtil {

    public static byte extractValue(byte value, int startBit, int mask) {
        return (byte) ((value >> startBit) & (mask));
    }
}
