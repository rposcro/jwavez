package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import lombok.Getter;

@Getter
public class ZWaveResponse {

    private SerialCommand serialCommand;
    private int length;

    public ZWaveResponse(ImmutableBuffer frameBuffer) {
        this.length = frameBuffer.getByte(SerialFrameConstants.FRAME_OFFSET_LENGTH) & 0xff;
        this.serialCommand = SerialCommand.ofCode(frameBuffer.getByte(SerialFrameConstants.FRAME_OFFSET_COMMAND));
    }
}