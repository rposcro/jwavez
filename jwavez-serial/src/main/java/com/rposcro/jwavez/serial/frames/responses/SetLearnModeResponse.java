package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.utils.FieldsUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SET_LEARN_MODE)
public class SetLearnModeResponse extends ZWaveResponse {

    @Getter
    private boolean requestAccepted;

    public SetLearnModeResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.requestAccepted = FieldsUtil.byteBoolean(frameBuffer.getByte(FRAME_OFFSET_PAYLOAD));
    }
}
