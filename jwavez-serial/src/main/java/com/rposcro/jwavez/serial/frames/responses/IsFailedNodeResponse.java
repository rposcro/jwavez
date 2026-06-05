package com.rposcro.jwavez.serial.frames.responses;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.utils.FieldsUtil;
import lombok.Getter;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

@Getter
@ResponseFrameModel(function = SerialCommand.IS_FAILED_NODE_ID)
public class IsFailedNodeResponse extends ZWaveResponse {

    private boolean failed;

    public IsFailedNodeResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.failed = FieldsUtil.byteBoolean(frameBuffer.getByte(FRAME_OFFSET_PAYLOAD));
    }
}
