package com.rposcro.jwavez.serial.frames.requests;

import static com.rposcro.jwavez.serial.enums.SerialCommand.RF_POWER_LEVEL_GET;

import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class GetRFPowerLevelRequest extends ZWaveRequest {

  public static SerialRequest createGetRFPowerLevelRequest() {
    return nonPayloadRequest(RF_POWER_LEVEL_GET);
  }
}
