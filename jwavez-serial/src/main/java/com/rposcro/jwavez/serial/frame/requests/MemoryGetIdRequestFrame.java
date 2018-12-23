package com.rposcro.jwavez.serial.frame.requests;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.RequestFrameModel;
import com.rposcro.jwavez.serial.frame.SOFRequestFrame;

@RequestFrameModel(function = SerialCommand.MEMORY_GET_ID)
public class MemoryGetIdRequestFrame extends SOFRequestFrame {

  public MemoryGetIdRequestFrame() {
    super(SerialCommand.MEMORY_GET_ID);
  }
}
