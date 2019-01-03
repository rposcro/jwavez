package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.RF_POWER_LEVEL_GET)
public class GetRFPowerLevelResponseFrame extends SOFResponseFrame {

  private byte powerLevel;

  public GetRFPowerLevelResponseFrame(byte[] buffer) {
    super(buffer);
    this.powerLevel = buffer[OFFSET_PAYLOAD];
  }
}
