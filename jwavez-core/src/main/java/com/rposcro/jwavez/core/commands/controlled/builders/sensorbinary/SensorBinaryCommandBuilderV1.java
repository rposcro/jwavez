package com.rposcro.jwavez.core.commands.controlled.builders.sensorbinary;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class SensorBinaryCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(),
                SensorBinaryCommandType.SENSOR_BINARY_GET.getCode());
    }
}
