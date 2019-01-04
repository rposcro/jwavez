package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_CONTROLLER_CAPABILITIES)
public class GetControllerCapabilitiesResponseFrame extends SOFResponseFrame {

  private static final byte MASK_IS_SECONDARY = 0x01;
  private static final byte MASK_ON_OTHER_NETWORK = 0x02;
  private static final byte MASK_NODE_ID_SERVER_PRESENT = 0x04;
  private static final byte MASK_IS_REAL_PRIMARY = 0x08;
  private static final byte MASK_IS_SUC = 0x10;

  private byte capabilities;

  public GetControllerCapabilitiesResponseFrame(byte[] buffer) {
    super(buffer);
    this.capabilities = buffer[OFFSET_PAYLOAD];
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
