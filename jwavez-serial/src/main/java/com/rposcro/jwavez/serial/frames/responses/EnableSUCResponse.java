package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;

@ResponseFrameModel(function = SerialCommand.ENABLE_SUC)
public class EnableSUCResponse extends ZWaveResponse {

    public EnableSUCResponse(ViewBuffer frameBuffer) {
        super(frameBuffer);
    }
}
