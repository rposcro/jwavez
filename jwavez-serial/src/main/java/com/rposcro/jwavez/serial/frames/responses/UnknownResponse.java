package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;

@Getter
public class UnknownResponse extends ZWaveResponse {

    private byte[] payload;
    private byte crc;

    public UnknownResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.payload = frameBuffer.cloneRemainingBytes(frameBuffer.available() - 1);
        this.crc = frameBuffer.skip(frameBuffer.available() - 1).nextByte();
    }
}
