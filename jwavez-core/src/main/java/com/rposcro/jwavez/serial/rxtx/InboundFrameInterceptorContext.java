package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InboundFrameInterceptorContext {

  private SOFFrame frame;
  private boolean stopProcessing;

  public void stopProcessing() {
    this.stopProcessing = true;
  }
}
