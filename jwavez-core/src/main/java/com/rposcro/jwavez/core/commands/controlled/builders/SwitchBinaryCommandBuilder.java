package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.core.classes.CommandClass;

public class SwitchBinaryCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SWITCH_BINARY.getCode(), SwitchBinaryCommandType.BINARY_SWITCH_GET.getCode());
  }

  public ZWaveControlledCommand buildSetCommand(byte value, byte duration) {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_SWITCH_BINARY.getCode(), SwitchBinaryCommandType.BINARY_SWITCH_SET.getCode(), value, duration);
  }
}
