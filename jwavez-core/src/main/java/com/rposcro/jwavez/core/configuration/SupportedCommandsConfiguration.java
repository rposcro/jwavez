package com.rposcro.jwavez.core.configuration;

import com.rposcro.jwavez.core.commands.JwzSupportedCommandParser;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolversRegistry;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;

public class SupportedCommandsConfiguration {

  public SupportedCommandResolversRegistry supportedCommandResolversRegistry() {
    return SupportedCommandResolversRegistry.instance();
  }

  public JwzSupportedCommandParser inboundCommandParser() {
    return JwzSupportedCommandParser.builder()
        .supportedCommandsRegistry(supportedCommandResolversRegistry())
        .build();
  }

  public SupportedCommandDispatcher supportedCommandDispatcher() {
    return new SupportedCommandDispatcher();
  }
}
