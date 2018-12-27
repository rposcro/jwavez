package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.frame.SOFFrame;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundFrameProcessor implements Runnable {

  private SerialCommunicationBroker communicationBroker;
  private List<InboundFrameInterceptor> interceptors;

  @Builder
  public InboundFrameProcessor(SerialCommunicationBroker communicationBroker) {
    this.communicationBroker = communicationBroker;
    this.interceptors = new LinkedList<>();
  }

  public void addInterceptor(InboundFrameInterceptor interceptor) {
    interceptors.add(interceptor);
  }

  public void insertAsFirst(InboundFrameInterceptor interceptor) {
    interceptors.add(0, interceptor);
  }

  public void run() {
    try {
      SOFFrame inboundFrame = communicationBroker.takeInboundFrame();
      InboundFrameInterceptorContext context = InboundFrameInterceptorContext.builder()
          .frame(inboundFrame)
          .stopProcessing(false)
          .build();
      for (InboundFrameInterceptor interceptor: interceptors) {
        interceptor.intercept(context);
        if (context.isStopProcessing()) {
          break;
        }
      }
    } catch(InterruptedException e) {
      log.warn("Inbound frame processor interrupted!");
    }
  }
}
