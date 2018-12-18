package com.rposcro.jwavez.serial.frame;

public abstract class SOFResponseFrame extends SOFFrame {

  protected SOFResponseFrame(byte[] buffer) {
    super(buffer);
  }
}
