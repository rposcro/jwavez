package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.commands.enums.SensorBinaryCommandType;

public class SensorBinaryControlledCommand {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_GET.getCode());
  }

  public ZWaveControlledCommand buildGetSupportedSensorCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR.getCode());
  }
}
