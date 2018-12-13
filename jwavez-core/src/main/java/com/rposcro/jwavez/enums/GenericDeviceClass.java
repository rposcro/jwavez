package com.rposcro.jwavez.enums;

import com.rposcro.jwavez.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.utils.EncodableConstant;

public enum GenericDeviceClass implements EncodableConstant {

  GENERIC_TYPE_NOT_KNOWN(0x00),
  GENERIC_TYPE_REMOTE_CONTROLLER(0x01),
  GENERIC_TYPE_STATIC_CONTOLLER(0x02),
  GENERIC_TYPE_AV_CONTROL_POINT(0x03),
  GENERIC_TYPE_DISPLAY(0x04),
  GENERIC_TYPE_THERMOSTAT(0x08),
  GENERIC_TYPE_WINDOW_COVERING(0x09),
  GENERIC_TYPE_REPEATER_SLAVE(0x0F),
  GENERIC_TYPE_BINARY_SWITCH(0x10),
  GENERIC_TYPE_MULTILEVEL_SWITCH(0x11),
  GENERIC_TYPE_REMOTE_SWITCH(0x12),
  GENERIC_TYPE_TOGGLE_SWITCH(0x13),
  GENERIC_TYPE_Z_IP_GATEWAY(0x14),
  GENERIC_TYPE_Z_IP_NODE(0x15),
  GENERIC_TYPE_VENTILATION(0x16),
  GENERIC_TYPE_SENSOR_BINARY(0x20),
  GENERIC_TYPE_MULTILEVEL_SENSOR(0x21),
  GENERIC_TYPE_PULSE_METER(0x30),
  GENERIC_TYPE_METER(0x31),
  GENERIC_TYPE_ENTRY_CONTROL(0x40),
  GENERIC_TYPE_SEMI_INTEROPERABLE(80),
  GENERIC_TYPE_ALARM_SENSOR(161),
  GENERIC_TYPE_NON_INTEROPERABLE(0xFF),
  ;

  private GenericDeviceClass(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static GenericDeviceClass ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(GenericDeviceClass.class, code);
  }
}
