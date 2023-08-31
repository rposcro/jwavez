package com.rposcro.jwavez.core.commands.controlled.builders.sensorbinary;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class SensorBinaryCommandBuilderV2 extends SensorBinaryCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand(byte sensorType) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(),
                SensorBinaryCommandType.SENSOR_BINARY_GET.getCode(),
                sensorType
        );
    }

    public ZWaveControlledCommand buildGetSupportedSensorCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(),
                SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR.getCode());
    }
}
