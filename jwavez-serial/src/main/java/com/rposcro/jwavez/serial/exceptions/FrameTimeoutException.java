package com.rposcro.jwavez.serial.exceptions;

public class FrameTimeoutException extends SerialStreamException {

  public FrameTimeoutException(String message) {
    super(message);
  }

  public FrameTimeoutException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public FrameTimeoutException(Throwable throwable) {
    super(throwable);
  }

  public FrameTimeoutException(Throwable throwable, String message) {
    super(throwable, message);
  }
}
