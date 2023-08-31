package com.rposcro.jwavez.serial.frames.callbacks;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.CallbackFrameModel;
import com.rposcro.jwavez.serial.utils.FieldsUtil;
import lombok.Getter;

@Getter
@CallbackFrameModel(function = SerialCommand.SET_SUC_NODE_ID)
public class SetSUCNodeIdCallback extends FlowCallback {

    private boolean successful;

    public SetSUCNodeIdCallback(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.successful = FieldsUtil.byteBoolean(frameBuffer.nextByte());
    }
}
