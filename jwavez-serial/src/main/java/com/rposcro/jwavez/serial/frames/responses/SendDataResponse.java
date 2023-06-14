package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.utils.FieldsUtil;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.SEND_DATA)
public class SendDataResponse extends SolicitedCallbackResponse {

    @Getter
    private boolean requestAccepted;

    public SendDataResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.requestAccepted = FieldsUtil.byteBoolean(frameBuffer.getByte(FRAME_OFFSET_PAYLOAD));
    }

    @Override
    public boolean isSolicitedCallbackToFollow() {
        return requestAccepted;
    }
}
