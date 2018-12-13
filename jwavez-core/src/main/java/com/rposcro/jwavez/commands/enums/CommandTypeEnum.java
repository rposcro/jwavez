package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;

public interface CommandTypeEnum {

  default CommandClass getCommandClass() {
    return this.getClass().getAnnotation(CommandConstant.class).commandClass();
  }

  default byte getCode() {
    return CommandConstantsRegistry.codeOfConstant(this);
  }
}
