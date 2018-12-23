package com.rposcro.jwavez.serial.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;

public enum AddNodeToNeworkMode implements EncodableConstant {
  
  ADD_NODE_ANY(0x01),
  ADD_NODE_CONTROLLER(0x02),
  ADD_NODE_SLAVE(0x03),
  ADD_NODE_EXISTING(0x04),
  ADD_NODE_STOP(0x05),
  ADD_NODE_STOP_FAILED(0x06),
  OPTION_HIGH_POWER(0x80)
  ;

  private AddNodeToNeworkMode(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static AddNodeToNeworkMode ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(AddNodeToNeworkMode.class, code);
  }
}
