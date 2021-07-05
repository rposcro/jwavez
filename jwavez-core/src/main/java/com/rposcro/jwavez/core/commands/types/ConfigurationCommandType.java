package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_CONFIGURATION)
public enum ConfigurationCommandType implements CommandType {

  // v1
  CONFIGURATION_SET(0x04),
  CONFIGURATION_GET(0x05),
  CONFIGURATION_REPORT(0x06),

  // v2
  CONFIGURATION_BULK_SET(0x07),
  CONFIGURATION_BULK_GET(0x08),
  CONFIGURATION_BULK_REPORT(0x09),

  // v3
  CONFIGURATION_NAME_GET(0x0A),
  CONFIGURATION_NAME_REPORT(0x0B),
  CONFIGURATION_INFO_GET(0x0C),
  CONFIGURATION_INFO_REPORT(0x0D),
  CONFIGURATION_PROPERTIES_GET(0x0E),
  CONFIGURATION_PROPERTIES_REPORT(0x0F),

  // v4
  CONFIGURATION_DEFAULT_RESET(0x01),
  ;

  private ConfigurationCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
