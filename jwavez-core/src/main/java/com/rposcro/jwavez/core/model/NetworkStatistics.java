package com.rposcro.jwavez.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NetworkStatistics {

    private int transmittedFramesCount;
    private int backOffsCount;
    private int receivedCorrectFramesCount;
    private int lcrErrorsCount;
    private int crcErrorsCount;
    private int foreignHomeIdCount;
}
