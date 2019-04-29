package com.rposcro.jwavez.serial.exceptions;

public class FatalSerialException extends RuntimeException {

  public FatalSerialException(String message) {
    super(message);
  }

  public FatalSerialException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public FatalSerialException(Throwable throwable) {
    super(throwable);
  }

  public FatalSerialException(Throwable throwable, String message) {
    super(message, throwable);
  }

  public FatalSerialException(Throwable throwable, String message, Object... replacements) {
    super(String.format(message, replacements), throwable);
  }
}
