package com.rposcro.jwavez.serial.controllers.helpers;

import java.util.concurrent.atomic.AtomicInteger;

public class CallbackFlowIdDispatcher {

    private static CallbackFlowIdDispatcher SHARED_INSTANCE;

    private AtomicInteger nextFlowId;

    public CallbackFlowIdDispatcher() {
        nextFlowId = new AtomicInteger(1);
    }

    public byte nextFlowId() {
        return (byte) (nextFlowId.getAndAccumulate(1, (cur, inc) -> cur == 255 ? 1 : cur + 1));
    }

    public static CallbackFlowIdDispatcher shared() {
        return SHARED_INSTANCE != null ? SHARED_INSTANCE : (SHARED_INSTANCE = new CallbackFlowIdDispatcher());
    }
}
