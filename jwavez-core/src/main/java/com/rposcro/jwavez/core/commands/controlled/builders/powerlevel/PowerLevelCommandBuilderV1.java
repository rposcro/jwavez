package com.rposcro.jwavez.core.commands.controlled.builders.powerlevel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class PowerLevelCommandBuilderV1 {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_POWERLEVEL.getCode(), PowerLevelCommandType.POWER_LEVEL_GET.getCode());
  }

  public ZWaveControlledCommand buildSetCommand(byte value, byte timeout) {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_POWERLEVEL.getCode(), PowerLevelCommandType.POWER_LEVEL_SET.getCode(), value, timeout);
  }
}
