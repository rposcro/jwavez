package com.rposcro.jwavez.core.handlers;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;

public interface SupportedCommandHandler<T extends ZWaveSupportedCommand> {

  void handleCommand(T command);
}
