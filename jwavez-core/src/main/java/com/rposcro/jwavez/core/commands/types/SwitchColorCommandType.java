package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SWITCH_COLOR)
public enum SwitchColorCommandType implements CommandType {

  SWITCH_COLOR_SUPPORTED_GET(0x01),
  SWITCH_COLOR_SUPPORTED_REPORT(0x02),
  SWITCH_COLOR_GET(0x03),
  SWITCH_COLOR_REPORT(0x04),
  SWITCH_COLOR_SET(0x05),
  SWITCH_COLOR_START_LEVEL_CHANGE(0x06),
  SWITCH_COLOR_STOP_LEVEL_CHANGE(0x07),
  ;

  SwitchColorCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
