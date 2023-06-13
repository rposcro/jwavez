package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

public abstract class SolicitedCallbackResponse extends ZWaveResponse {

    public SolicitedCallbackResponse(ImmutableBuffer viewBuffer) {
        super(viewBuffer);
    }

    public abstract boolean isSolicitedCallbackToFollow();
}
