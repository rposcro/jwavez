package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandBuilderException;

public class SwitchColorCommandBuilder {

  public ZWaveControlledCommand buildSupportedGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SWITCH_COLOR.getCode(), SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_GET.getCode());
  }

  public ZWaveControlledCommand buildGetCommand(byte componentId) {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SWITCH_COLOR.getCode(), SwitchColorCommandType.SWITCH_COLOR_GET.getCode(), componentId);
  }

  public ZWaveControlledCommand buildSetCommand(byte duration, byte... values) {
    if (values.length % 2 != 0) {
      throw new CommandBuilderException("Odd parameters count: " + values.length);
    }

    byte[] buffer = new byte[values.length + 4];
    buffer[0] = CommandClass.CMD_CLASS_SWITCH_COLOR.getCode();
    buffer[1] = SwitchColorCommandType.SWITCH_COLOR_SET.getCode();
    int pairLen = values.length / 2;
    buffer[2] = (byte) pairLen;
    int bufIdx = 3;
    for (int idx = 0; idx < values.length; idx++) {
      buffer[bufIdx++] = values[idx];
    }
    buffer[bufIdx] = duration;
    return new ZWaveControlledCommand(buffer);
  }

  public ZWaveControlledCommand buildSetWarmRGBWCommand(byte red, byte green, byte blue, byte white, byte duration) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_SWITCH_COLOR.getCode(),
        SwitchColorCommandType.SWITCH_COLOR_SET.getCode(),
        (byte) 4, (byte) 0, white, (byte) 2, red, (byte) 3, green, (byte) 4, blue, duration);
  }

  public ZWaveControlledCommand buildSetColdRGBWCommand(byte red, byte green, byte blue, byte white, byte duration) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_SWITCH_COLOR.getCode(),
        SwitchColorCommandType.SWITCH_COLOR_SET.getCode(),
        (byte) 4, (byte) 1, white, (byte) 2, red, (byte) 3, green, (byte) 4, blue, duration);
  }

  public ZWaveControlledCommand buildSetRGBCommand(byte red, byte green, byte blue, byte duration) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_SWITCH_COLOR.getCode(),
        SwitchColorCommandType.SWITCH_COLOR_SET.getCode(),
        (byte) 3, (byte) 2, red, (byte) 3, green, (byte) 4, blue, duration);
  }
}
