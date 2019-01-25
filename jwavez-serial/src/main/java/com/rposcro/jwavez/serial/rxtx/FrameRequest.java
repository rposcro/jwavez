package com.rposcro.jwavez.serial.rxtx;

import java.nio.ByteBuffer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FrameRequest {

  private ByteBuffer frameData;
  private boolean retransmissionDisabled;
  private boolean responseExpected;
}
