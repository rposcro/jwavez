package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FrameRequest {

  private FrameBuffer frameData;
  private boolean retransmissionDisabled;
  private boolean responseExpected;
}
