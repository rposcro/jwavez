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

    public SupportedCommandDispatcher registerAllCommandsHandler(SupportedCommandListener commandHandler) {
        List<SupportedCommandListener> handlers = listenersPerCommandType.computeIfAbsent(null, type -> new LinkedList<>());
        if (!handlers.contains(commandHandler)) {
            handlers.add(commandHandler);
        }
        return this;
    }

    public SupportedCommandDispatcher registerHandler(CommandType commandType, SupportedCommandListener commandHandler) {
        List<SupportedCommandListener> handlers = listenersPerCommandType.computeIfAbsent(commandType, type -> new LinkedList<>());
        if (!handlers.contains(commandHandler)) {
            handlers.add(commandHandler);
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
