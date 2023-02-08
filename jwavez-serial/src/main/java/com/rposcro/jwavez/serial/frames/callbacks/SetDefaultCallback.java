package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SET_DEFAULT)
public class SetDefaultCallback extends FlowCallback {

    public SetDefaultCallback(ViewBuffer frameBuffer) {
        super(frameBuffer);
    }
}
