package com.rposcro.jwavez.core.classes;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;

public enum BasicDeviceClass implements EncodableConstant {

  BASIC_TYPE_NOT_KNOWN(0),
  BASIC_TYPE_CONTROLLER(1),
  BASIC_TYPE_STATIC_CONTROLLER(2),
  BASIC_TYPE_SLAVE(3),
  BASIC_TYPE_ROUTING_SLAVE(4);
  ;

  private BasicDeviceClass(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static BasicDeviceClass ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(BasicDeviceClass.class, code);
  }
}
