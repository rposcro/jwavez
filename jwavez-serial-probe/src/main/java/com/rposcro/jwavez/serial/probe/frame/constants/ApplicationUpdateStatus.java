package com.rposcro.jwavez.serial.probe.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;

public enum ApplicationUpdateStatus implements EncodableConstant {

  APP_UPDATE_STATUS_NEW_ID_ASSIGNED(0x40),
  APP_UPDATE_STATUS_DELETE_DONE(0x20),
  APP_UPDATE_STATUS_NODE_INFO_REQ_DONE(0x82),
  APP_UPDATE_STATUS_NODE_INFO_REQ_FAILED(0x81),
  APP_UPDATE_STATUS_NODE_INFO_RECEIVED(0x84),
  APP_UPDATE_STATUS_SUC_ID(0x10),
  APP_UPDATE_ROUTING_PENDING(0x20),
  ;

  private ApplicationUpdateStatus(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static ApplicationUpdateStatus ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(ApplicationUpdateStatus.class, code);
  }
}
