package com.rposcro.jwavez.core.exceptions;

public class JWaveZException extends RuntimeException {

  public JWaveZException(String message) {
    super(message);
  }

  public JWaveZException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
