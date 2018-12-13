package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.commands.enums.ConfigurationCommandType;

public class ConfigurationControlledCommand extends ControlledZWaveCommand {

  private ConfigurationControlledCommand(byte... commandPayload) {
    super(commandPayload);
  }

  public static ConfigurationControlledCommand buildGetParameterCommand(int parameterNumber) {
    return new ConfigurationControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_GET.getCode(),
        (byte) parameterNumber);
  }

  public static ConfigurationControlledCommand buildSetParameterCommand(int parameterNumber, byte value) {
    return new ConfigurationControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_SET.getCode(),
        (byte) parameterNumber,
        sizeField(1, false),
        value
    );
  }

  public static ConfigurationControlledCommand buildBulkGetParameterCommand(int parameterOffset, int parametersCount) {
    return new ConfigurationControlledCommand(
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
