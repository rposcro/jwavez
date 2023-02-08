package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;

public final class ZWaveControlledCommand {

    @Getter
    private ImmutableBuffer payloadBuffer;

    public ZWaveControlledCommand(byte... commandPayload) {
        this.payloadBuffer = ImmutableBuffer.overBuffer(commandPayload, 0, commandPayload.length);
    }

    public int getPayloadLength() {
        return this.payloadBuffer.getLength();
    }

    public ImmutableBuffer getPayload() {
        return this.payloadBuffer;
    }
}
