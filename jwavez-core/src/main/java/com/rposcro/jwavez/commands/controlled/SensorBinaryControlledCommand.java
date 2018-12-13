package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.commands.enums.SensorBinaryCommandType;

public class SensorBinaryControlledCommand extends ControlledZWaveCommand {

  private SensorBinaryControlledCommand(byte... commandPayload) {
    super(commandPayload);
  }

  public static SensorBinaryControlledCommand buildGetCommand() {
    return new SensorBinaryControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_GET.getCode());
  }

  public static SensorBinaryControlledCommand buildGetSupportedSensorCommand() {
    return new SensorBinaryControlledCommand(CommandClass.CMD_CLASS_SENSOR_BINARY.getCode(), SensorBinaryCommandType.SENSOR_BINARY_SUPPORTED_GET_SENSOR.getCode());
  }
}
