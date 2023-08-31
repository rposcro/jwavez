package com.rposcro.jwavez.serial.exceptions;

public class RxTxException extends SerialException {

    public RxTxException(String message) {
        super(message);
    }

    public RxTxException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

    public RxTxException(Throwable throwable) {
        super(throwable);
    }

    public RxTxException(Throwable throwable, String message) {
        super(throwable, message);
    }
}
