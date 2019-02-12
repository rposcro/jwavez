package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.buffers.FrameBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SerialRequest {

  private FrameBuffer frameData;
  private SerialCommand serialCommand;
  private boolean retransmissionDisabled;
  private boolean responseExpected;
}
