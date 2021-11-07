package com.rposcro.jwavez.tools.utils;

public class SerialUtils {

    public static final long DEFAULT_TIMEOUT = 5_000;

    private static byte callbackFlowId;

    public static byte nextFlowId() {
        return ++callbackFlowId == 0 ? ++callbackFlowId : callbackFlowId;
    }
}
