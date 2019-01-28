package com.rposcro.jwavez.serial.rxtx;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RxTxConfiguration {

  @Builder.Default private long ackTimeout = 1600;
  @Builder.Default private long responseTimeout = 5000;
  @Builder.Default private long frameCompleteTimeout = 1500;
  @Builder.Default private long maxRetriesCount = 4;
  @Builder.Default private long retryDelayBias = 300;
  @Builder.Default private long retryDelayFactor = 1000;
}
