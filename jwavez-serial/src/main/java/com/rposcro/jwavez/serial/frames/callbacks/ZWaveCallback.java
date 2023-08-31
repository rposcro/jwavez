package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import lombok.Getter;

@Getter
public abstract class ZWaveCallback {

    private SerialCommand serialCommand;
    private int length;

    public ZWaveCallback(ImmutableBuffer frameBuffer) {
        this.length = frameBuffer.getByte(SerialFrameConstants.FRAME_OFFSET_LENGTH) & 0xff;
        this.serialCommand = SerialCommand.ofCode(frameBuffer.getByte(SerialFrameConstants.FRAME_OFFSET_COMMAND));
    }

    public String asFineString() {
        return String.format("%s(%02x)", serialCommand.name(), serialCommand.getCode());
    }
}