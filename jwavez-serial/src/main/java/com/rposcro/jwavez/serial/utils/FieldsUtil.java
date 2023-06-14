package com.rposcro.jwavez.serial.utils;

public class FieldsUtil {

    public static final byte REQUEST_STATE_FAILED = 0x00;
    public static final byte REQUEST_STATE_SUCCESSFUL = 0x01;

    public static byte booleanByte(boolean flag) {
        return (byte) (flag ? REQUEST_STATE_SUCCESSFUL : REQUEST_STATE_FAILED);
    }

    public static boolean byteBoolean(byte flag) {
        return flag != REQUEST_STATE_FAILED;
    }

    public static int asInt(byte chunk) {
        return chunk & 0xFF;
    }
}
