package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.commands.enums.BasicCommandType;
import com.rposcro.jwavez.core.enums.CommandClass;

public class BasicCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_BASIC.getCode(),
        BasicCommandType.BASIC_GET.getCode()
    );
  }
}
