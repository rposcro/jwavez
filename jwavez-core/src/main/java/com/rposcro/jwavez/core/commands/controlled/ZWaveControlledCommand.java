package com.rposcro.jwavez.core.commands.controlled;

import lombok.Getter;

public final class ZWaveControlledCommand {

    @Getter
    private byte[] payload;

    public ZWaveControlledCommand(byte... commandPayload) {
        this.payload = commandPayload;
    }

    public int getPayloadLength() {
        return this.payload.length;
    }
}
