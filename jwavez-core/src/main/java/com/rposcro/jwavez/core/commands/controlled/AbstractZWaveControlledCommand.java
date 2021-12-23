package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;

public abstract class AbstractZWaveControlledCommand {

  @Getter
  private ImmutableBuffer payloadBuffer;

  protected AbstractZWaveControlledCommand(byte... commnadPayload) {
    this.payloadBuffer = ImmutableBuffer.overBuffer(commnadPayload, 0, commnadPayload.length);
  }

  public int getPayloadLength() {
    return this.payloadBuffer.getLength();
  }

  public ImmutableBuffer getPayload() {
    return this.payloadBuffer;
  }
}