package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

public interface CommandType {

  default CommandClass getCommandClass() {
    return this.getClass().getAnnotation(CommandTypeEnum.class).commandClass();
  }

  default byte getCode() {
    return CommandTypesRegistry.codeOfType(this);
  }

  String name();
}
