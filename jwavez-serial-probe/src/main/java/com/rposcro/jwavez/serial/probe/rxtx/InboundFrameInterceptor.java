package com.rposcro.jwavez.serial.probe.rxtx;

@FunctionalInterface
public interface InboundFrameInterceptor {

  void intercept(InboundFrameInterceptorContext context);
}
