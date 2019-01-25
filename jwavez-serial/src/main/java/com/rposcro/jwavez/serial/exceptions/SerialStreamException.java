package com.rposcro.jwavez.serial.exceptions;

public class SerialStreamException extends SerialException {

  public SerialStreamException(String message) {
    super(message);
  }

  public SerialStreamException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public SerialStreamException(Throwable throwable) {
    super(throwable);
  }

  public SerialStreamException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
