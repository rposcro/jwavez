package com.rposcro.jwavez.serial.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;

public enum RemoveNodeFromNeworkStatus implements EncodableConstant {

  REMOVE_NODE_STATUS_LEARN_READY(0x01),
  REMOVE_NODE_STATUS_NODE_FOUND(0x02),
  REMOVE_NODE_STATUS_REMOVING_SLAVE(0x03),
  REMOVE_NODE_STATUS_REMOVING_CONTROLLER(0x04),
  REMOVE_NODE_STATUS_DONE(0x06),
  REMOVE_NODE_STATUS_FAILED(0x07)
  ;

  RemoveNodeFromNeworkStatus(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static RemoveNodeFromNeworkStatus ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(RemoveNodeFromNeworkStatus.class, code);
  }
}
