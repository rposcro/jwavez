package com.rposcro.jwavez.serial.probe.rxtx;

import com.rposcro.jwavez.serial.probe.frame.SOFFrame;
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
