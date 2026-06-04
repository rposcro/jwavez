package com.rposcro.jwavez.tools.shell.spring;

import org.springframework.shell.core.command.*;
import org.springframework.shell.core.utils.Utils;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
    Customized Spring Shell's Help command
 */
public class CustomizedHelpCommand extends AbstractCommand {

    public CustomizedHelpCommand() {
        super("help", "Show help about available commands", "Built-In Commands");
    }

    @Override
    public String getHelp() {
        return "help [command]\n\n"
                + "Display help about available commands. If a command is specified, display detailed help about that command.";
    }

    @Override
    public ExitStatus doExecute(CommandContext commandContext) throws Exception {
        PrintWriter outputWriter = commandContext.outputWriter();
        CommandRegistry commandRegistry = commandContext.commandRegistry();
        String helpMessage = formatAvailableCommands(commandRegistry);
        List<CommandArgument> arguments = commandContext.parsedInput().arguments();
        String commandName = String.join(" ", arguments.stream().map(CommandArgument::value).toList());
        Command command = commandRegistry.getCommandByName(commandName);
        if (command != null) {
            helpMessage = getHelpMessageForCommand(command);
        }
        else {
            Command aliasCommand = commandRegistry.getCommandByAlias(commandName);
            if (aliasCommand != null) {
                helpMessage = getHelpMessageForCommand(aliasCommand);
            }
        }
        outputWriter.println(helpMessage);
        outputWriter.flush();
        return ExitStatus.OK;
    }

    private String getHelpMessageForCommand(Command command) {
        StringBuilder helpMessageBuilder = new StringBuilder();
        appendName(command, helpMessageBuilder);
        appendSynopsis(command, helpMessageBuilder);
        appendOptions(command, helpMessageBuilder);
        appendAliases(command, helpMessageBuilder);
        return helpMessageBuilder.toString();
    }

    private void appendName(Command command, StringBuilder helpMessageBuilder) {
        helpMessageBuilder.append("NAME\n")
                .append("\t")
                .append(command.getName())
                .append(" - ")
                .append(command.getDescription())
                .append("\n\n");
    }

    private void appendSynopsis(Command command, StringBuilder helpMessageBuilder) {
        List<CommandOption> options = command.getOptions();
        helpMessageBuilder.append("SYNOPSIS\n").append("\t").append(command.getName()).append(" ");
        if (!options.isEmpty()) {
            for (CommandOption option : options) {
                if (!option.required()) {
                    helpMessageBuilder.append("[");
                }
                if (option.longName() != null) {
                    helpMessageBuilder.append("--").append(option.longName());
                }
                else {
                    helpMessageBuilder.append("-").append(option.shortName());
                }
                helpMessageBuilder.append(" ").append(option.type().getSimpleName());
                helpMessageBuilder.append(option.required() ? " " : "] ");
            }
        }
        helpMessageBuilder.append("\n\n");
    }

    private void appendOptions(Command command, StringBuilder helpMessageBuilder) {
        List<CommandOption> options = command.getOptions();
        helpMessageBuilder.append("OPTIONS\n");
        if (!options.isEmpty()) {
            for (CommandOption option : options) {
                helpMessageBuilder.append("\t");
                if (option.longName() != null) {
                    helpMessageBuilder.append("--").append(option.longName());
                }
                if (option.shortName() != ' ') {
                    helpMessageBuilder.append(" or -").append(option.shortName());
                }
                helpMessageBuilder.append(" ").append(option.type().getSimpleName()).append("\n");
                helpMessageBuilder.append("\t").append(option.description()).append("\n");
                if (option.required()) {
                    helpMessageBuilder.append("\t").append("[Mandatory]").append("\n\n");
                }
                else {
                    helpMessageBuilder.append("\t").append("[Optional, default = ");
                    String defaultValue = option.defaultValue();
                    Class<?> optionType = option.type();
                    if (defaultValue == null && optionType.isPrimitive()) {
                        defaultValue = Utils.getDefaultValueForPrimitiveType(optionType).toString();
                    }
                    helpMessageBuilder.append(defaultValue).append("]\n\n");
                }
            }
        }
        helpMessageBuilder.append("\t--help or -h").append("\n");
        helpMessageBuilder.append("\thelp for ").append(command.getName()).append("\n");
        helpMessageBuilder.append("\t").append("[Optional]").append("\n").append("\n");
    }

    private static void appendAliases(Command command, StringBuilder helpMessageBuilder) {
        List<String> aliases = command.getAliases();
        if (!aliases.isEmpty()) {
            helpMessageBuilder.append("ALIASES\n");
            helpMessageBuilder.append("\t").append(String.join(", ", aliases)).append("\n");
        }
    }

    /*
        Customized from original Spring Shell's Utils
     */
    public String formatAvailableCommands(CommandRegistry commandRegistry) {
        StringBuilder stringBuilder = new StringBuilder("AVAILABLE COMMANDS");
        stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
        Set<Command> commands = getCommands(commandRegistry);
        List<String> groups = commands.stream()
                .filter(command -> !command.isHidden())
                .map(Command::getGroup)
                .distinct()
                .sorted()
                .toList();
        for (String group : groups) {
            stringBuilder.append(group).append(System.lineSeparator());
            for (Command command : commands.stream()
                    .filter(c -> !c.isHidden())
                    .filter(c -> c.getGroup().equals(group))
                    .sorted(Comparator.comparing(Command::getName))
                    .toList()) {
                stringBuilder.append("\t")
                        .append(command.getAvailabilityProvider().get().isAvailable() ? "" : "[-] ")
                        .append(command.getName())
                        .append(command.getAliases().isEmpty() ? "" : ", " + String.join(", ", command.getAliases()))
                        .append(": ")
                        .append(command.getDescription())
                        .append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    /*
        Customized from original Spring Shell's Utils
     */
    private Set<Command> getCommands(CommandRegistry commandRegistry) {
        Set<Command> commands = new HashSet<>(commandRegistry.getCommands());
        commands.add(Utils.QUIT_COMMAND);
        return commands;
    }
}
