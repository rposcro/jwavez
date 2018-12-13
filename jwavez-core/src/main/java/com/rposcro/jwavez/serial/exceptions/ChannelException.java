package com.rposcro.jwavez.serial.exceptions;

public class ChannelException extends SerialException {

  public ChannelException(String message) {
    super(message);
  }

  public ChannelException(Exception e) {
    super(e);
  }

  public ChannelException(String message, Exception e) {
    super(message, e);
  }
}
