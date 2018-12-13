package com.rposcro.jwavez.serial.rxtx;

@FunctionalInterface
public interface InboundFrameInterceptor {

  void intercept(InboundFrameInterceptorContext context);
}
