package com.rposcro.jwavez.core.commands.controlled.builders.version;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class VersionCommandBuilderV1 {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_GET.getCode());
  }

  public ZWaveControlledCommand buildCommandClassGetCommand(CommandClass commandClass) {
    return new ZWaveControlledCommand(
            CommandClass.CMD_CLASS_VERSION.getCode(),
            VersionCommandType.VERSION_COMMAND_CLASS_GET.getCode(),
            commandClass.getCode());
  }
}
