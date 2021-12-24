package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

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
