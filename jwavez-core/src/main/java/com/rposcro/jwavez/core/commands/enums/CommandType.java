package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;

public interface CommandType {

  default CommandClass getCommandClass() {
    return this.getClass().getAnnotation(CommandTypeEnum.class).commandClass();
  }

  default byte getCode() {
    return CommandTypesRegistry.codeOfType(this);
  }
}
