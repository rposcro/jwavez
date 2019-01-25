package com.rposcro.jwavez.serial.probe.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import java.util.Optional;

public enum FrameCategory implements EncodableConstant {

  ACK(0x06),
  NAK(0x15),
  CAN(0x18),
  SOF(0x01)
  ;

  FrameCategory(int categoryCode) {
    EncodableConstantsRegistry.registerConstant(this, (byte) categoryCode);
  }

  public static FrameCategory ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(FrameCategory.class, code);
  }

  public static Optional<FrameCategory> ofCodeOptional(byte code) {
    return EncodableConstantsRegistry.optionalConstantOfCode(FrameCategory.class, code);
  }
}
