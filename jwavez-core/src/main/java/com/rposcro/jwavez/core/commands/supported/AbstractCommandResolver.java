package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.CommandType;
import com.rposcro.jwavez.core.commands.enums.CommandTypesRegistry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCommandResolver<T extends CommandType> implements ZWaveSupportedCommandResolver<T> {

  private Set<T> supportedCommands;

  protected AbstractCommandResolver(Collection<T> supportedCommands) {
    this.supportedCommands = new HashSet<>(supportedCommands);
  }

  public Set<T> supportedCommands() {
    return supportedCommands;
  }
}
