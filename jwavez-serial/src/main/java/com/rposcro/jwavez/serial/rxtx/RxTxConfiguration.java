package com.rposcro.jwavez.serial.rxtx;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RxTxConfiguration {

  @Builder.Default private long frameAckTimeout = 1600;
  @Builder.Default private long frameResponseTimeout = 5000;
  @Builder.Default private long frameCompleteTimeout = 1500;

  @Builder.Default private long requestRetriesMaxCount = 4;
  @Builder.Default private long requestRetryDelayBias = 300;
  @Builder.Default private long requestRetryDelayFactor = 1000;

  @Builder.Default private long portReconnectMaxCount = 10;
  @Builder.Default private long portReconnectDelayBias = 500;
  @Builder.Default private long portReconnectDelayFactor = 500;
}
