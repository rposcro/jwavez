package com.rposcro.jwavez.serial.exceptions;

public class FlowException extends SerialException {

    public FlowException(String message) {
        super(message);
    }

    public FlowException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

    public FlowException(Throwable throwable) {
        super(throwable);
    }

    public FlowException(Throwable throwable, String message) {
        super(throwable, message);
    }

    public FlowException(Throwable throwable, String message, Object... replacements) {
        super(throwable, String.format(message, replacements));
    }
}
