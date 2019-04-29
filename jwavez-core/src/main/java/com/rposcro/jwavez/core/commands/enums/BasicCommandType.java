package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_BASIC)
public enum BasicCommandType implements CommandType {

  BASIC_SET(0x01),
  BASIC_GET(0x02),
  BASIC_REPORT(0x03),
  ;

  BasicCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
