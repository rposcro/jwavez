package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;

public interface CommandType {

  default CommandClass getCommandClass() {
    return this.getClass().getAnnotation(CommandTypeEnum.class).commandClass();
  }

  default byte getCode() {
    return CommandTypesRegistry.codeOfConstant(this);
  }
}
