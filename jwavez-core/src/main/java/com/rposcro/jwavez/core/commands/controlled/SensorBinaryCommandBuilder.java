package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.commands.enums.SensorBinaryCommandType;

public class SensorBinaryCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_GET.getCode());
  }

  public ZWaveControlledCommand buildGetSupportedSensorCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR.getCode());
  }
}
