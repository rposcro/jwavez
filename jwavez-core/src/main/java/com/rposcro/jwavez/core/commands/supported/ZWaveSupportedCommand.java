package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.commands.enums.CommandType;

public abstract class ZWaveSupportedCommand<C extends CommandType> {

  protected static final int OFFSET_COMMAND_CLASS = 0;
  protected static final int OFFSET_COMMAND = 1;

  private C commandType;

  protected ZWaveSupportedCommand(C commandType) {
    this.commandType = commandType;
  }

  public CommandClass commandClass() {
    return this.commandType.getCommandClass();
  }

  public C commandType() {
    return commandType;
  }
}
