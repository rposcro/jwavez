package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.model.LibraryType;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_LIBRARY_TYPE)
public class GetLibraryTypeResponse extends ZWaveResponse {

    private LibraryType libraryType;

    public GetLibraryTypeResponse(ImmutableBuffer frameBuffer) {
        super(frameBuffer);
        this.libraryType = LibraryType.ofCode(frameBuffer.getByte(FRAME_OFFSET_PAYLOAD));
    }
}
