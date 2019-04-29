package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_CONTROLLER_CAPABILITIES)
public class GetControllerCapabilitiesResponse extends ZWaveResponse {

  private static final byte MASK_IS_SECONDARY = 0x01;
  private static final byte MASK_ON_OTHER_NETWORK = 0x02;
  private static final byte MASK_NODE_ID_SERVER_PRESENT = 0x04;
  private static final byte MASK_IS_REAL_PRIMARY = 0x08;
  private static final byte MASK_IS_SUC = 0x10;

  private byte capabilities;

  public GetControllerCapabilitiesResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.capabilities = frameBuffer.get(FRAME_OFFSET_PAYLOAD);
  }

  public boolean isRealPrimary() {
    return (capabilities & MASK_IS_REAL_PRIMARY) > 0;
  }

  public boolean isSecondary() {
    return (capabilities & MASK_IS_SECONDARY) > 0;
  }

  public boolean isSUC() {
    return (capabilities & MASK_IS_SUC) > 0;
  }

  public boolean isSIS() {
    return (capabilities & MASK_NODE_ID_SERVER_PRESENT) > 0;
  }

  public boolean isOnOtherNetwork() {
    return (capabilities & MASK_ON_OTHER_NETWORK) > 0;
  }
}
