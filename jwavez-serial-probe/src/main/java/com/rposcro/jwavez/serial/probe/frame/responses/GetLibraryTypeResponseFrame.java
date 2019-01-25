package com.rposcro.jwavez.serial.probe.frame.responses;

import com.rposcro.jwavez.serial.probe.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.LibraryType;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_LIBRARY_TYPE)
public class GetLibraryTypeResponseFrame extends SOFResponseFrame {

  private LibraryType libraryType;

  public GetLibraryTypeResponseFrame(byte[] buffer) {
    super(buffer);
    this.libraryType = LibraryType.ofCode(buffer[OFFSET_PAYLOAD]);
  }
}
