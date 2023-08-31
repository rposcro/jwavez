package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SENSOR_BINARY)
public enum SensorBinaryCommandType implements CommandType {

    // v1
    SENSOR_BINARY_GET(0x02),
    SENSOR_BINARY_REPORT(0x03),

    // v2
    SENSOR_BINARY_SUPPORTED_GET_SENSOR(0x01),
    SENSOR_BINARY_SUPPORTED_SENSOR_REPORT(0x04),
    ;

    private SensorBinaryCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
