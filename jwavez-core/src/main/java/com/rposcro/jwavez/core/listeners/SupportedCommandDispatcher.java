package com.rposcro.jwavez.core.listeners;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedCommandDispatcher {

    private Map<CommandType, List<SupportedCommandListener>> listenersPerCommandType = new HashMap<>();

    public SupportedCommandDispatcher registerCommandsListener(SupportedCommandListener commandListener) {
        List<SupportedCommandListener> listeners = listenersPerCommandType.computeIfAbsent(null, type -> new LinkedList<>());
        if (!listeners.contains(commandListener)) {
            listeners.add(commandListener);
        }
        return this;
    }

    public SupportedCommandDispatcher registerListener(CommandType commandType, SupportedCommandListener commandListener) {
        List<SupportedCommandListener> listeners = listenersPerCommandType.computeIfAbsent(commandType, type -> new LinkedList<>());
        if (!listeners.contains(commandListener)) {
            listeners.add(commandListener);
        }
        return this;
    }

    public void dispatchCommand(ZWaveSupportedCommand command) {
        if (log.isDebugEnabled()) {
            log.debug("Command to dispatch: {}", command.asNiceString());
        } else {
            log.info("Command to dispatch: {} {}", command.getCommandClass(), command.getCommandType());
        }

        Optional.ofNullable(listenersPerCommandType.get(command.getCommandType()))
                .ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handleCommand(command)));
        Optional.ofNullable(listenersPerCommandType.get(null))
                .ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handleCommand(command)));
    }
}
