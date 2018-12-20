package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;

public class ConfigurationCommandBuilder {

  public ZWaveControlledCommand buildGetParameterCommand(int parameterNumber) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_GET.getCode(),
        (byte) parameterNumber);
  }

  public ZWaveControlledCommand buildSetParameterCommand(int parameterNumber, byte value) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_SET.getCode(),
        (byte) parameterNumber,
        sizeField(1, false),
        value
    );
  }

  public ZWaveControlledCommand buildBulkGetParameterCommand(int parameterOffset, int parametersCount) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_BULK_GET.getCode(),
        (byte) (parameterOffset >> 8),
        (byte) (parameterOffset),
        (byte) (parametersCount));
  }

  private static byte sizeField(int valueSize, boolean resetToDefault) {
    return (byte) ((valueSize & 0x7) | (resetToDefault ? 0x80 : 0));
  }
}
