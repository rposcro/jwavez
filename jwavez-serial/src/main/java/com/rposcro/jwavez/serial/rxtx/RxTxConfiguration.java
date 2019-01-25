package com.rposcro.jwavez.serial.rxtx;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RxTxConfiguration {

  private long ackTimeout = 1600;
  private long responseTimeout = 5000;
  private long maxRetriesCount = 4;
  private long retryDelayBias = 300;
  private long retryDelayFactor = 1000;
}
