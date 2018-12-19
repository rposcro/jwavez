package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.utils.ImmutableBuffer;
import lombok.Getter;

public final class ZWaveControlledCommand {

  @Getter
  private ImmutableBuffer payloadBuffer;

  ZWaveControlledCommand(byte... commnadPayload) {
    this.payloadBuffer = ImmutableBuffer.overBuffer(commnadPayload, 0, commnadPayload.length);
  }

  public int getPayloadLength() {
    return this.payloadBuffer.getLength();
  }

  public ImmutableBuffer getPayload() {
    return this.payloadBuffer;
  }
}
