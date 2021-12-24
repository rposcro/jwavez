package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.classes.CommandClass;

public class VersionCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_GET.getCode());
  }

  public ZWaveControlledCommand buildCommandClassGetCommand(CommandClass commandClass) {
    return new ZWaveControlledCommand(
            CommandClass.CMD_CLASS_VERSION.getCode(),
            VersionCommandType.VERSION_COMMAND_CLASS_GET.getCode(),
            commandClass.getCode());
  }

  public ZWaveControlledCommand buildCapabilitiesGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_CAPABILITIES_GET.getCode());
  }

  public ZWaveControlledCommand buildZWaveSoftwareGetVersion() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_ZWAVE_SOFTWARE_GET.getCode());
  }
}
