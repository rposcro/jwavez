package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.commands.enums.CommandType;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import java.util.Set;

public interface ZWaveSupportedCommandResolver<T extends CommandType> {

  ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer);

  Set<T> supportedCommands();
  default CommandClass supportedCommandClass() {
    return this.getClass().getAnnotation(SupportedCommandResolver.class).commandClass();
  }
}
