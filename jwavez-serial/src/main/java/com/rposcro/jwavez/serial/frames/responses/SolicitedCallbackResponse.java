package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;

public abstract class SolicitedCallbackResponse extends ZWaveResponse {

    public SolicitedCallbackResponse(ViewBuffer viewBuffer) {
        super(viewBuffer);
    }

    public abstract boolean isSolicitedCallbackToFollow();
}
