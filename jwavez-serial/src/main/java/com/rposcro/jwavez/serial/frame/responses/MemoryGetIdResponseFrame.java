package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.MEMORY_GET_ID)
public class MemoryGetIdResponseFrame extends SOFResponseFrame {

  private static final int OFFSET_HOME_ID = 4;
  private static final int OFFSET_NODE_ID = 8;

  private int homeId;
  private short nodeId;

  public MemoryGetIdResponseFrame(byte[] buffer) {
    super(buffer);
    this.homeId = (((int) buffer[OFFSET_HOME_ID]) << 24)
        | (((int) buffer[OFFSET_HOME_ID + 1]) << 16)
        | (((int) buffer[OFFSET_HOME_ID + 2]) << 8)
        | (((int) buffer[OFFSET_HOME_ID + 3]));
    this.nodeId = (short) buffer[OFFSET_NODE_ID];
  }
}
