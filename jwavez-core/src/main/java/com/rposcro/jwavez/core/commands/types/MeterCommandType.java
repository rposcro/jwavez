package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_METER)
public enum MeterCommandType implements CommandType {

  METER_GET(0x01),
  METER_REPORT(0x02),
  METER_SUPPORTED_GET(0x03),
  METER_SUPPORTED_REPORT(0x04),
  METER_RESET(0x05)
  ;

  MeterCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
