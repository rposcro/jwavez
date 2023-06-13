package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_CAPABILITIES)
public class GetCapabilitiesResponse extends ZWaveResponse {

    private byte serialAppVersion;
    private byte serialAppRevision;
    private int manufacturerId;
    private int manufacturerProductType;
    private int manufacturerProductId;
    List<Integer> serialCommands;

    public GetCapabilitiesResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        frameBuffer.position(FRAME_OFFSET_PAYLOAD);
        this.serialAppVersion = frameBuffer.nextByte();
        this.serialAppRevision = frameBuffer.nextByte();
        this.manufacturerId = frameBuffer.nextUnsignedWord();
        this.manufacturerProductType = frameBuffer.nextUnsignedWord();
        this.manufacturerProductId = frameBuffer.nextUnsignedWord();
        this.serialCommands = parseSerialCommandsMask(frameBuffer);
    }

    public List<Integer> parseSerialCommandsMask(ImmutableBuffer frameBuffer) {
        List<Integer> functions = new LinkedList<>();
        int functionId = 1;
        while (frameBuffer.available() > 1) {
            byte chunk = frameBuffer.nextByte();
            int bitMask = 1;
            for (int bit = 0; bit < 8; bit++) {
                if ((chunk & bitMask) > 0) {
                    functions.add(functionId);
                }
                functionId++;
                bitMask <<= 1;
            }
        }
        return functions;
    }
}
