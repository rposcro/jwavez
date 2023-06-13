package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;

@Getter
public abstract class FlowCallback extends ZWaveCallback {

    private byte callbackFlowId;

    public FlowCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        callbackFlowId = frameBuffer.next();
    }

    public String asFineString() {
        return String.format("%s(%02x) clbckId(%02x)",
                getSerialCommand().name(), getSerialCommand().getCode(), callbackFlowId);
    }
}