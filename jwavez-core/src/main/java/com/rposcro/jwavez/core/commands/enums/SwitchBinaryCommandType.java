package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SWITCH_BINARY)
public enum SwitchBinaryCommandType implements CommandType {

  BINARY_SWITCH_SET(0x01),
  BINARY_SWITCH_GET(0x02),
  BINARY_SWITCH_REPORT(0x03),
  ;

  private SwitchBinaryCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
