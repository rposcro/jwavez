package com.rposcro.jwavez.core.listeners;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;

public interface SupportedCommandListener<T extends ZWaveSupportedCommand> {

    void handleCommand(T command);
}
