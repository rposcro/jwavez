package com.rposcro.jwavez.serial.exceptions;

public class StreamException extends RxTxException {

  public StreamException(String message) {
    super(message);
  }

  public StreamException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public StreamException(Throwable throwable) {
    super(throwable);
  }

  public StreamException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
