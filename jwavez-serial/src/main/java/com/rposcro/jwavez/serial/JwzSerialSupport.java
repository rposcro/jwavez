package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.core.buffer.ByteBufferManager;

public class JwzSerialSupport {

    private static JwzSerialSupport DEFAULT_INSTANCE;

    private final SerialRequestFactory serialRequestFactory;

    private JwzSerialSupport() {
        this.serialRequestFactory = new SerialRequestFactory(new ByteBufferManager());
    }

    public static JwzSerialSupport defaultSupport() {
        return DEFAULT_INSTANCE == null ? DEFAULT_INSTANCE = new JwzSerialSupport() : DEFAULT_INSTANCE;
    }

    public SerialRequestFactory serialRequestFactory() {
        return serialRequestFactory;
    }
}
