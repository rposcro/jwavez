package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_PROTOCOL_STATUS)
public class GetProtocolStatusResponse extends ZWaveResponse {

    private byte returnValue;

    public GetProtocolStatusResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.returnValue = frameBuffer.getByte(FRAME_OFFSET_PAYLOAD);
    }
}
