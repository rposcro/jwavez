package com.rposcro.jwavez.serial.frame;

import lombok.Getter;

@Getter
public abstract class SOFCallbackFrame extends SOFFrame {

  public final static int OFFSET_FUNC_ID = OFFSET_PAYLOAD;

  private byte callbackFunctionId;
  private int payloadSize;

  protected SOFCallbackFrame(byte[] buffer) {
    super(buffer);
    this.callbackFunctionId = buffer[OFFSET_FUNC_ID];
    this.payloadSize = buffer.length - 5;
  }
}
