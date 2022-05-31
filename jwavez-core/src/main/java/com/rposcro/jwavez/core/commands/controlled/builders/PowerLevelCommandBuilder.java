package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;

public class PowerLevelCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_POWERLEVEL.getCode(), PowerLevelCommandType.POWER_LEVEL_GET.getCode());
  }

  public ZWaveControlledCommand buildSetCommand(byte value, byte timeout) {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_POWERLEVEL.getCode(), PowerLevelCommandType.POWER_LEVEL_SET.getCode(), value, timeout);
  }
}