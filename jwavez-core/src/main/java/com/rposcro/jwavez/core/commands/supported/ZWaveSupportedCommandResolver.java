package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.CommandType;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import java.util.Set;

public interface ZWaveSupportedCommandResolver<T extends CommandType> {

  ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer);

  Set<T> supportedCommands();
  default CommandClass supportedCommandClass() {
    return this.getClass().getAnnotation(SupportedCommandResolver.class).commandClass();
  }
}
