package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC)
public enum ManufacturerSpecificCommandType implements CommandType {

  MANUFACTURER_SPECIFIC_GET(0x04),
  MANUFACTURER_SPECIFIC_REPORT(0x05),
  ;

  ManufacturerSpecificCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
