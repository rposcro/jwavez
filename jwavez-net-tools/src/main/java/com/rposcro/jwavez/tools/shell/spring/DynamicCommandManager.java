package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.events.ScopeChangedEvent;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DynamicCommandManager {

    private static final Set<String> DYNAMIC_GROUPS = Set.of(
            CommandGroup.DONGLE, CommandGroup.NETWORK, CommandGroup.NODE, CommandGroup.TALK);

    @Autowired
    @Lazy
    private CommandRegistry commandRegistry;

    private Map<ShellScope, List<Command>> commandsByScope;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        customizeHelpCommand();
        commandsByScope = commandRegistry.getCommands().stream()
                .filter(command -> DYNAMIC_GROUPS.contains(command.getGroup()))
                .collect(Collectors.groupingBy(this::scopeOf));
        commandsByScope.keySet()
                .forEach(this::unregisterCommandsOfScope);
    }

    @EventListener(ScopeChangedEvent.class)
    public void onScopeChangedEvent(ScopeChangedEvent event) {
        unregisterCommandsOfScope(event.getScopeBefore());
        registerCommandsOfScope(event.getScopeAfter());
    }

    private void customizeHelpCommand() {
        Optional.ofNullable(commandRegistry.getCommandByName("help"))
                .ifPresent(originCommand -> {
                    commandRegistry.unregisterCommand(originCommand);
                    commandRegistry.registerCommand(new CustomizedHelpCommand());
                });
    }

    private void unregisterCommandsOfScope(ShellScope scope) {
        List<Command> commands = Optional.ofNullable(commandsByScope.get(scope))
                .orElse(List.of());
        commands.forEach(commandRegistry::unregisterCommand);
    }

    private void registerCommandsOfScope(ShellScope scope) {
        List<Command> commands = Optional.ofNullable(commandsByScope.get(scope))
                .orElse(List.of());
        commands.forEach(commandRegistry::registerCommand);
    }

    private ShellScope scopeOf(Command command) {
        return ShellScope.valueOf(command.getGroup().toUpperCase());
    }
}