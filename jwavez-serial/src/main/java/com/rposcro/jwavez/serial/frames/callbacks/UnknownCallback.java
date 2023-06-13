package com.rposcro.jwavez.serial.frames.callbacks;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;

@Getter
public class UnknownCallback extends ZWaveCallback {

    private byte[] payload;
    private byte crc;

    public UnknownCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.payload = frameBuffer.cloneBytes(frameBuffer.available() - 1);
        this.crc = frameBuffer.skip(frameBuffer.available() - 1).nextByte();
    }
}
