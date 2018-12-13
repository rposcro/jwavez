package com.rposcro.jwavez.serial.frame;

import lombok.Getter;

@Getter
public abstract class SOFCallbackFrame extends SOFFrame {

  private byte callbackFunctionId;

  protected SOFCallbackFrame(byte[] buffer) {
    super(buffer);
    this.callbackFunctionId = buffer[OFFSET_PAYLOAD];
  }
}
