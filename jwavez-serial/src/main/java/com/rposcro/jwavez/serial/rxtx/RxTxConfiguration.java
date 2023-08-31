package com.rposcro.jwavez.serial.rxtx;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RxTxConfiguration {

    private static RxTxConfiguration defaultConfiguration;

    @Builder.Default
    private long frameAckTimeout = 1600;
    @Builder.Default
    private long frameResponseTimeout = 5000;
    @Builder.Default
    private long frameCompleteTimeout = 1500;

    @Builder.Default
    private long requestRetriesMaxCount = 4;
    @Builder.Default
    private long requestRetryDelayBias = 300;
    @Builder.Default
    private long requestRetryDelayFactor = 1000;

    @Builder.Default
    private long portReconnectMaxCount = 10;
    @Builder.Default
    private long portReconnectDelayBias = 500;
    @Builder.Default
    private long portReconnectDelayFactor = 500;

    @Builder.Default
    private long routerPollDelay = 50;

    public static RxTxConfiguration defaultConfiguration() {
        return defaultConfiguration == null ? defaultConfiguration = new RxTxConfiguration() : defaultConfiguration;
    }
}
