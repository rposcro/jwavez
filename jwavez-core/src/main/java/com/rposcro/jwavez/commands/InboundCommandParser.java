package com.rposcro.jwavez.commands;

import com.rposcro.jwavez.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.commands.supported.ZWaveSupportedCommandResolver;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class InboundCommandParser {

  private SupportedCommandResolversRegistry supportedCommandsRegistry;

  public <T extends ZWaveSupportedCommand> T parseCommand(ImmutableBuffer payload) throws CommandNotSupportedException {
    CommandClass commandClass = CommandClass.ofCode(payload.getByte(0));
    ZWaveSupportedCommandResolver commandResolver = supportedCommandsRegistry.findResolver(commandClass);
    return (T) commandResolver.resolve(payload);
  }
}
