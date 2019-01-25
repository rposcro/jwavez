package com.rposcro.jwavez.serial.enums;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum FrameCategory implements EncodableConstant {

  ACK(0x06),
  NAK(0x15),
  CAN(0x18),
  SOF(0x01)
  ;

  FrameCategory(int categoryCode) {
    EncodableConstantsRegistry.registerConstant(this, (byte) categoryCode);
  }
}
