package com.rposcro.jwavez.commands.configuration;

import com.rposcro.jwavez.commands.InboundCommandParser;
import com.rposcro.jwavez.commands.supported.SupportedCommandResolversRegistry;

public class SupportedCommandsConfiguration {

  public SupportedCommandResolversRegistry supportedCommandResolversRegistry() {
    return SupportedCommandResolversRegistry.instance();
  }

  public InboundCommandParser inboundCommandParser() {
    return InboundCommandParser.builder()
        .supportedCommandsRegistry(supportedCommandResolversRegistry())
        .build();
  }
}
