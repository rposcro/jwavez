package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_LIBRARY_TYPE)
public class GetTypeLibraryRequestFrame extends SOFRequestFrame {

  public GetTypeLibraryRequestFrame() {
    super(GetTypeLibraryRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
