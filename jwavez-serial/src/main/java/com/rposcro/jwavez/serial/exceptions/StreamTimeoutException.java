package com.rposcro.jwavez.serial.exceptions;

public class StreamTimeoutException extends StreamException {

  public StreamTimeoutException(String message) {
    super(message);
  }

  public StreamTimeoutException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public StreamTimeoutException(Throwable throwable) {
    super(throwable);
  }

  public StreamTimeoutException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
