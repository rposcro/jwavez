package com.rposcro.jwavez.core.exceptions;

public class AssertionException extends JWaveZException {

    public AssertionException(String message) {
        super(message);
    }

    public AssertionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
