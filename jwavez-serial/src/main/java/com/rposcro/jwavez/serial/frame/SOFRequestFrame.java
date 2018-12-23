package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.frame.constants.FrameType;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;

public class SOFRequestFrame extends SOFFrame {

  protected SOFRequestFrame(SerialCommand serialCommand) {
    super(FrameType.REQ, serialCommand);
  }

  protected SOFRequestFrame(SerialCommand serialCommand, int payloadSize, byte[] payload) {
    super(FrameType.REQ, serialCommand, payloadSize, 0, payload);
  }

  protected SOFRequestFrame(SerialCommand serialCommand, byte... payload) {
    super(FrameType.REQ, serialCommand, payload.length, 0, payload);
  }

  protected SOFRequestFrame(byte[] buffer) {
    super(buffer);
  }

}
