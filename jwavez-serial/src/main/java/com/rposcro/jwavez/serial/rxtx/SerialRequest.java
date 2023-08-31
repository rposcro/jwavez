package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SerialRequest {

    @Builder.Default
    private long id = System.nanoTime();
    private ImmutableBuffer frameData;
    private SerialCommand serialCommand;
    private boolean retransmissionDisabled;
    private boolean responseExpected;
    private byte callbackFlowId;
}
