package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.classes.CommandClass;

public class BasicCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_BASIC.getCode(),
        BasicCommandType.BASIC_GET.getCode()
    );
  }
}
