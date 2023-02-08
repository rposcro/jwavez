package com.rposcro.jwavez.serial.exceptions;

public class FrameException extends SerialException {

    public FrameException(String message) {
        super(message);
    }

    public FrameException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

    public FrameException(Throwable throwable) {
        super(throwable);
    }

    public FrameException(Throwable throwable, String message) {
        super(throwable, message);
    }

    public FrameException(Throwable throwable, String message, Object... replacements) {
        super(throwable, String.format(message, replacements));
    }
}
