package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

public abstract class SolicitedCallbackResponse extends ZWaveResponse {

    public SolicitedCallbackResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
    }

    public abstract boolean isSolicitedCallbackToFollow();
}
