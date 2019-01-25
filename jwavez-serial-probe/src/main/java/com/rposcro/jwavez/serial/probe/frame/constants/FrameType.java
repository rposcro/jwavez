package com.rposcro.jwavez.serial.probe.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum FrameType implements EncodableConstant {

  REQ(0x00),
  RES(0x01)
  ;

  FrameType(int typeCode) {
    EncodableConstantsRegistry.registerConstant(this, (byte) typeCode);
  }

  public static FrameType ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(FrameType.class, code);
  }
}
