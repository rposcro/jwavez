package com.rposcro.jwavez.serial.exceptions;

public class FrameParseException extends FrameException {

  public FrameParseException(String message) {
    super(message);
  }

  public FrameParseException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public FrameParseException(Throwable throwable) {
    super(throwable);
  }

  public FrameParseException(Throwable throwable, String message) {
    super(throwable, message);
  }

  public FrameParseException(Throwable throwable, String message, Object... replacements) {
    super(throwable, String.format(message, replacements));
  }
}
