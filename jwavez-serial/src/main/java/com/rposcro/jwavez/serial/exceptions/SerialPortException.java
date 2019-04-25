package com.rposcro.jwavez.serial.exceptions;

public class SerialPortException extends RxTxException {

  public SerialPortException(String message) {
    super(message);
  }

  public SerialPortException(String message, Object... replacements) {
    super(String.format(message, replacements));
  }

  public SerialPortException(Throwable throwable) {
    super(throwable);
  }

  public SerialPortException(Throwable throwable, String message) {
    super(throwable, message);
  }

  public SerialPortException(Throwable throwable, String message, Object... replacements) {
    super(throwable, String.format(message, replacements));
  }
}
