package com.rposcro.jwavez.samples;

import java.util.concurrent.atomic.AtomicInteger;

public class AbstractExample {

  public static final String DEFAULT_DEVICE = "/dev/cu.usbmodem144201";
  public static final String ENV_NAME = "JWAVEZ_DEVICE";

  private AtomicInteger flowId = new AtomicInteger(1);

  protected String determineDevice() {
    String device = System.getenv(ENV_NAME);
    return device == null ? DEFAULT_DEVICE : device;
  }

  protected byte nextFlowId() {
    int id = flowId.accumulateAndGet(1, (current, update) -> {
      if (++current > 254) {
        current = 1;
      };
      return current;
    });
    return (byte) id;
  }
}
