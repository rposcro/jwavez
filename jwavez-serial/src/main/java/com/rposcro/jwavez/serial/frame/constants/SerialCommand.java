package com.rposcro.jwavez.serial.frame.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum SerialCommand implements EncodableConstant {

  GET_INIT_DATA(0x02),
  APPLICATION_COMMAND_HANDLER(0x04),
  GET_CAPABILITIES(0x07),
  SERIAL_API_SETUP(0x0B),
  SEND_DATA(0x13),
  GET_VERSION(0x15),
  SEND_DATA_ABORT(0x16),
  MEMORY_GET_ID(0x20),
  APPLICATION_UPDATE(0x49),
  ADD_NODE_TO_NETWORK(0x4A),
  REMOVE_NODE_FROM_NETWORK(0x4B),
  ENABLE_SUC(0x52),
  SET_SUC_NODE_ID(0x54),
  GET_SUC_NODE_ID(0x56),
  REQUEST_NODE_INFO(0x60),

  ZSTICK_SET_CONFIG(0xF2),
  ZSTICK_GET_CONFIG(0xF3),
  ;

  SerialCommand(int functionId) {
    EncodableConstantsRegistry.registerConstant(this, (byte) functionId);
  }

  public static SerialCommand ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(SerialCommand.class, code);
  }
}
