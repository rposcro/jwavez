package com.rposcro.jwavez.commands.controlled;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

abstract class ControlledZWaveCommand implements ZWaveControlledCommand {

  private static final byte[] emptyCommandPayload = new byte[0];

  @Getter
  @Setter(AccessLevel.PROTECTED)
  private byte[] payloadBuffer;

  protected ControlledZWaveCommand(byte[] commnadPayload) {
    this.payloadBuffer = commnadPayload;
  }

  protected ControlledZWaveCommand() {
    this.payloadBuffer = emptyCommandPayload;
  }

  @Override
  public int getPayloadLength() {
    return this.payloadBuffer.length;
  }
}
