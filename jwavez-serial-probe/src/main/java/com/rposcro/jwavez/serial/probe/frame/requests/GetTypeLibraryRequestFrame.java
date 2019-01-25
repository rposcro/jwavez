package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;

@RequestFrameModel(function = SerialCommand.GET_LIBRARY_TYPE)
public class GetTypeLibraryRequestFrame extends SOFRequestFrame {

  public GetTypeLibraryRequestFrame() {
    super(GetTypeLibraryRequestFrame.class.getAnnotation(RequestFrameModel.class).function());
  }
}
