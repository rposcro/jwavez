package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_ZWAVE_PLUS_INFO)
public enum ZWavePlusInfoCommandType implements CommandType {

  ZWAVE_PLUS_INFO_GET(0x01),
  ZWAVE_PLUS_INFO_REPORT(0x02)
  ;

  private ZWavePlusInfoCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
