package com.rposcro.jwavez.serial.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum LearnStatus implements EncodableConstant {

  LEARN_STATUS_STARTED(0x01),  // guessing??
  LEARN_STATUS_DONE(0x06),
  LEARN_STATUS_FAILED(0x07),
  LEARN_STATUS_SECURITY_FAILED(0x09),
  ;

  LearnStatus(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static LearnStatus ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(LearnStatus.class, code);
  }
}
