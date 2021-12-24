package com.rposcro.jwavez.core.commands;

import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommandResolver;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class SupportedCommandParser {

  private static SupportedCommandParser DEFAULT_PARSER;

  private SupportedCommandResolversRegistry supportedCommandsRegistry;

  public <T extends ZWaveSupportedCommand> T parseCommand(ImmutableBuffer payload, NodeId sourceNodeId) throws CommandNotSupportedException {
    CommandClass commandClass = CommandClass.ofCode(payload.getByte(0));
    ZWaveSupportedCommandResolver commandResolver = supportedCommandsRegistry.findResolver(commandClass);
    return (T) commandResolver.resolve(payload, sourceNodeId);
  }

  public boolean isCommandSupported(ImmutableBuffer payload) {
    CommandClass commandClass = CommandClass.ofCode(payload.getByte(0));
    return supportedCommandsRegistry.isCommandClassSupported(commandClass);
  }

  public static SupportedCommandParser defaultParser() {
    if (DEFAULT_PARSER == null) {
      DEFAULT_PARSER = SupportedCommandParser.builder()
              .supportedCommandsRegistry(SupportedCommandResolversRegistry.instance())
              .build();
    }
    return DEFAULT_PARSER;
  }
}
