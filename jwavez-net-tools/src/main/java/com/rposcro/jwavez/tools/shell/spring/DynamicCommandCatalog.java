package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import lombok.Builder;
import org.springframework.shell.command.CommandAlias;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Copy of org.springframework.shell.command.CommandCatalog$DefaultCommandCatalog expanded
 * with dynamic command filtration
 */
public class DynamicCommandCatalog implements CommandCatalog {

    private static final String[] GROUPS_ALWAYS_ON = {"Built-In Commands", CommandGroup.GENERIC};

    private final Map<String, CommandRegistration> commandRegistrations = new HashMap<>();
    private final Collection<CommandResolver> resolvers = new ArrayList<>();
    private final ShellContext shellContext;
    private final JWaveZShellContext jWaveZShellContext;

    @Builder
    public DynamicCommandCatalog(Collection<CommandResolver> resolvers, ShellContext shellContext, JWaveZShellContext jWaveZShellContext) {
        this.shellContext = shellContext;
        this.jWaveZShellContext = jWaveZShellContext;
        if (resolvers != null) {
            this.resolvers.addAll(resolvers);
        }
    }

    @Override
    public void register(CommandRegistration... registration) {
        for (CommandRegistration r : registration) {
            String commandName = r.getCommand();
            commandRegistrations.put(commandName, r);
            for (CommandAlias a : r.getAliases()) {
                commandRegistrations.put(a.getCommand(), r);
            }
        }
    }

    @Override
    public void unregister(CommandRegistration... registration) {
        for (CommandRegistration r : registration) {
            String commandName = r.getCommand();
            commandRegistrations.remove(commandName);
            for (CommandAlias a : r.getAliases()) {
                commandRegistrations.remove(a.getCommand());
            }
        }
    }

    @Override
    public void unregister(String... commandName) {
        for (String n : commandName) {
            commandRegistrations.remove(n);
        }
    }

    @Override
    public Map<String, CommandRegistration> getRegistrations() {
        Map<String, CommandRegistration> regs = new HashMap<>();
        regs.putAll(commandRegistrations);
        for (CommandResolver resolver : resolvers) {
            resolver.resolve().stream().forEach(r -> {
                regs.put(r.getCommand(), r);
            });
        }
        return regs.entrySet().stream()
            .filter(filterByJwzShellContext(jWaveZShellContext))
            .filter(filterByInteractionMode(shellContext))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Filter registration entries by currently set mode. Having it set to ALL or null
     * effectively disables filtering as as we only care if mode is set to interactive
     * or non-interactive.
     */
    private static Predicate<Map.Entry<String, CommandRegistration>> filterByInteractionMode(ShellContext shellContext) {
        return e -> {
            InteractionMode mim = e.getValue().getInteractionMode();
            InteractionMode cim = shellContext != null ? shellContext.getInteractionMode() : InteractionMode.ALL;
            if (mim == null || cim == null || mim == InteractionMode.ALL) {
                return true;
            }
            else if (mim == InteractionMode.INTERACTIVE) {
                return cim == InteractionMode.INTERACTIVE || cim == InteractionMode.ALL;
            }
            else if (mim == InteractionMode.NONINTERACTIVE) {
                return cim == InteractionMode.NONINTERACTIVE || cim == InteractionMode.ALL;
            }
            return true;
        };
    }

    private static Predicate<Map.Entry<String, CommandRegistration>> filterByJwzShellContext(JWaveZShellContext jWaveZShellContext) {
        return registrationEntry -> {
            String registrationGroup = registrationEntry.getValue().getGroup();
            if (jWaveZShellContext.getShellScope().name().equalsIgnoreCase(registrationGroup)) {
                return true;
            }
            return Stream.of(GROUPS_ALWAYS_ON).anyMatch(groupOn -> groupOn.equals(registrationGroup));
        };
    }
}
