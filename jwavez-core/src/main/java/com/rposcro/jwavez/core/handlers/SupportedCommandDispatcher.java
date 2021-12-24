package com.rposcro.jwavez.core.handlers;

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

  private Map<CommandType, List<SupportedCommandHandler>> handlersPerCommandType = new HashMap<>();

  public SupportedCommandDispatcher registerAllCommandsHandler(SupportedCommandHandler commandHandler) {
    List<SupportedCommandHandler> handlers = handlersPerCommandType.computeIfAbsent(null, type -> new LinkedList<>());
    if (!handlers.contains(commandHandler)) {
      handlers.add(commandHandler);
    }
    return this;
  }

  public SupportedCommandDispatcher registerHandler(CommandType commandType, SupportedCommandHandler commandHandler) {
    List<SupportedCommandHandler> handlers = handlersPerCommandType.computeIfAbsent(commandType, type -> new LinkedList<>());
    if (!handlers.contains(commandHandler)) {
      handlers.add(commandHandler);
    }
    return this;
  }

  public void dispatchCommand(ZWaveSupportedCommand command) {
    log.info("Command to dispatch: {} {}", command.getCommandClass(), command.getCommandType());
    Optional.ofNullable(handlersPerCommandType.get(command.getCommandType()))
        .ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handleCommand(command)));
    Optional.ofNullable(handlersPerCommandType.get(null))
        .ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handleCommand(command)));
  }
}
