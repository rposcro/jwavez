package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.commands.enums.CommandTypeEnum;
import com.rposcro.jwavez.enums.CommandClass;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCommandResolver<T extends CommandTypeEnum> implements ZWaveSupportedCommandResolver<T> {

  private Set<T> supportedCommands;

  protected AbstractCommandResolver(Collection<T> supportedCommands) {
    this.supportedCommands = new HashSet<>(supportedCommands);
  }

  public Set<T> supportedCommands() {
    return supportedCommands;
  }
}
