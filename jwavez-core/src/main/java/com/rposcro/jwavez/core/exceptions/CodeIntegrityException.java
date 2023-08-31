package com.rposcro.jwavez.core.exceptions;

public class CodeIntegrityException extends JWaveZException {

    public CodeIntegrityException(String message) {
        super(message);
    }

    public CodeIntegrityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
