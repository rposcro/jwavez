package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;

import java.io.UnsupportedEncodingException;

import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_VERSION)
public class GetVersionResponse extends ZWaveResponse {

    private static final int VERSION_STRING_LENGTH = 11;

    private String version;
    private byte responseData;

    public GetVersionResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.version = decodeVersion(frameBuffer);
        this.responseData = frameBuffer.nextByte();
    }

    public String decodeVersion(ImmutableBuffer frameBuffer) {
        byte[] data = new byte[VERSION_STRING_LENGTH];
        for (int i = 0; i < VERSION_STRING_LENGTH; i++) {
            data[i] = frameBuffer.nextByte();
        }
        frameBuffer.nextByte(); // skip 0 char (c++ end of string)
        try {
            return new String(data, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            return new String(data);
        }
    }
}
