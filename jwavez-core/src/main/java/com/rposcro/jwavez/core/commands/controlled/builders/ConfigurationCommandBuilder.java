package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.constants.BitLength;
import com.rposcro.jwavez.core.utils.BytesUtil;

public class ConfigurationCommandBuilder {

  public ZWaveControlledCommand buildGetParameterCommand(int parameterNumber) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
        ConfigurationCommandType.CONFIGURATION_GET.getCode(),
        (byte) parameterNumber);
  }

  public ZWaveControlledCommand buildSetParameterCommand(int parameterNumber, byte value) {
    return buildSetParameterCommand(parameterNumber, value, BitLength.BIT_LENGTH_8);
  }

  public ZWaveControlledCommand buildSetParameterCommand(int parameterNumber, int value, BitLength valueSize) {
    byte[] payload = new byte[4 + valueSize.getBytesNumber()];
    payload[0] = CommandClass.CMD_CLASS_CONFIGURATION.getCode();
    payload[1] = ConfigurationCommandType.CONFIGURATION_SET.getCode();
    payload[2] = (byte) parameterNumber;
    payload[3] = sizeField(valueSize.getBytesNumber(), false);
    BytesUtil.writeMSBValue(payload, 4, valueSize, value);
    return new ZWaveControlledCommand(payload);
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
