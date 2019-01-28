package com.rposcro.jwavez.serial.exceptions;

public class OddFrameException extends SerialStreamException {

  public OddFrameException(String message) {
    super(message);
  }

  public OddFrameException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public OddFrameException(Throwable throwable) {
    super(throwable);
  }

  public OddFrameException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
