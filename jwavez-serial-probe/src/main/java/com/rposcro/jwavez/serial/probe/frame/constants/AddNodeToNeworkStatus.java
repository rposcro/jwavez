package com.rposcro.jwavez.serial.probe.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;

public enum AddNodeToNeworkStatus implements EncodableConstant {

  ADD_NODE_STATUS_LEARN_READY(0x01),
  ADD_NODE_STATUS_NODE_FOUND(0x02),
  ADD_NODE_STATUS_ADDING_SLAVE(0x03),
  ADD_NODE_STATUS_ADDING_CONTROLLER(0x04),
  ADD_NODE_STATUS_PROTOCOL_DONE(0x05),
  ADD_NODE_STATUS_DONE(0x06),
  ADD_NODE_STATUS_FAILED(0x07)
  ;
  
  private AddNodeToNeworkStatus(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static AddNodeToNeworkStatus ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(AddNodeToNeworkStatus.class, code);
  }
}
