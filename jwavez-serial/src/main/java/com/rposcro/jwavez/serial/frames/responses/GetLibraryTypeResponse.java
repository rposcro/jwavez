package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import com.rposcro.jwavez.serial.model.LibraryType;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_LIBRARY_TYPE)
public class GetLibraryTypeResponse extends ZWaveResponse {

  private LibraryType libraryType;

  public GetLibraryTypeResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.libraryType = LibraryType.ofCode(frameBuffer.get(FRAME_OFFSET_PAYLOAD));
  }
}
