package com.rposcro.jwavez.serial.probe.exceptions;

public class FrameException extends RuntimeException {

  public FrameException(String message) {
    super(message);
  }

  public FrameException(Exception e) {
    super(e);
  }

  public FrameException(String message, Exception e) {
    super(message, e);
  }
}
