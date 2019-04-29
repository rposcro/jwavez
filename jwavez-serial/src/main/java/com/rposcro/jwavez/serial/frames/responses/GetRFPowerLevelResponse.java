package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.RF_POWER_LEVEL_GET)
public class GetRFPowerLevelResponse extends ZWaveResponse {

  private byte powerLevel;

  public GetRFPowerLevelResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.powerLevel = frameBuffer.get(FRAME_OFFSET_PAYLOAD);
  }
}
