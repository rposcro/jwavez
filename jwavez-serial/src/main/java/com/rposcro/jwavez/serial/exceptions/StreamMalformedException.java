package com.rposcro.jwavez.serial.exceptions;

public class StreamMalformedException extends StreamException {

    public StreamMalformedException(String message) {
        super(message);
    }

    public StreamMalformedException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

    public StreamMalformedException(Throwable throwable) {
        super(throwable);
    }

    public StreamMalformedException(Throwable throwable, String message) {
        super(throwable, message);
    }
}
