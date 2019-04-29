package com.rposcro.jwavez.serial.probe.frame.requests;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.probe.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.MEMORY_GET_ID)
public class MemoryGetIdRequestFrame extends SOFRequestFrame {

  public MemoryGetIdRequestFrame() {
    super(SerialCommand.MEMORY_GET_ID);
  }
}
