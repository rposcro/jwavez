package com.rposcro.jwavez.core.exceptions;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.classes.CommandClass;
import lombok.Getter;

@Getter
public class CommandNotSupportedException extends JWaveZException {

  private final CommandClass commandClass;
  private final CommandType commandType;

  public CommandNotSupportedException(String message, CommandClass commandClass, CommandType commandType) {
    super(message);
    this.commandClass = commandClass;
    this.commandType = commandType;
  }

  public CommandNotSupportedException(String message) {
    super(message);
    this.commandClass = null;
    this.commandType = null;
  }

  public CommandNotSupportedException(CommandClass commandClass, CommandType commandType) {
    this("Command " + commandClass + "/" + commandType + " not supported!", commandClass, commandType);
  }

  public CommandNotSupportedException(CommandClass commandClass) {
    this("Command " + commandClass + " not supported!", commandClass, null);
  }
}
