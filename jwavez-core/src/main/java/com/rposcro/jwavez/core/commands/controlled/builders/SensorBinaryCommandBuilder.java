package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SensorBinaryCommandType;

public class SensorBinaryCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_GET.getCode());
  }

  public ZWaveControlledCommand buildGetSupportedSensorCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR.getCode());
  }
}
