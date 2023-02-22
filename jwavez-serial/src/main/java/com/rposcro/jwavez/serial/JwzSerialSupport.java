package com.rposcro.jwavez.serial;

public class JwzSerialSupport {

    private static JwzSerialSupport DEFAULT_INSTANCE;

    private final SerialRequestFactory serialRequestFactory;

    public JwzSerialSupport() {
        this.serialRequestFactory = new SerialRequestFactory();
    }

    public static JwzSerialSupport defaultSupport() {
        return DEFAULT_INSTANCE == null ? DEFAULT_INSTANCE = new JwzSerialSupport() : DEFAULT_INSTANCE;
    }

    public SerialRequestFactory serialRequestFactory() {
        return serialRequestFactory;
    }
}
