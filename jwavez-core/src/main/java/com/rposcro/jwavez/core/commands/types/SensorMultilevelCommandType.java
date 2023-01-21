package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SENSOR_MULTILEVEL)
public enum SensorMultilevelCommandType implements CommandType {

  SENSOR_MULTILEVEL_SUPPORTED_GET_SENSOR(0x01),
  SENSOR_MULTILEVEL_SUPPORTED_SENSOR_REPORT(0x02),
  SENSOR_MULTILEVEL_SUPPORTED_GET_SCALE(0x03),
  SENSOR_MULTILEVEL_GET(0x04),
  SENSOR_MULTILEVEL_REPORT(0x05),
  SENSOR_MULTILEVEL_SUPPORTED_SCALE_REPORT(0x06)
  ;

  SensorMultilevelCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
