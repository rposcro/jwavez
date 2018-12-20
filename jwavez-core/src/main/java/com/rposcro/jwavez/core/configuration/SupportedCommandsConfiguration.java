package com.rposcro.jwavez.core.configuration;

import com.rposcro.jwavez.core.commands.SupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;

public class SupportedCommandsConfiguration {

  public SupportedCommandResolversRegistry supportedCommandResolversRegistry() {
    return SupportedCommandResolversRegistry.instance();
  }

  public SupportedCommandParser inboundCommandParser() {
    return SupportedCommandParser.builder()
        .supportedCommandsRegistry(supportedCommandResolversRegistry())
        .build();
  }

  public SupportedCommandDispatcher supportedCommandDispatcher() {
    return new SupportedCommandDispatcher();
  }
}
