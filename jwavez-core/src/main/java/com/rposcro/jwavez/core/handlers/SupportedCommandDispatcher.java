package com.rposcro.jwavez.core.handlers;

import com.rposcro.jwavez.core.commands.enums.CommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedCommandDispatcher {

  private Map<CommandType, List<SupportedCommandHandler>> handlersPerCommandType = new HashMap<>();

  public void registerHandler(CommandType commandType, SupportedCommandHandler commandHandler) {
    List<SupportedCommandHandler> handlers = handlersPerCommandType.computeIfAbsent(commandType, type -> new LinkedList<>());
    if (!handlers.contains(commandHandler)) {
      handlers.add(commandHandler);
    }
  }

  public void dispatchCommand(ZWaveSupportedCommand command) {
    log.info("Command to dispatch: {} {}", command.commandClass(), command.commandType());
    Optional.ofNullable(handlersPerCommandType.get(command.commandType()))
        .ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handleCommand(command)));
  }
}