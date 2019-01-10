package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.options.HelpOptions;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;

public interface Command {

  CommandMetaReference[] references = {
    new CommandMetaReference(HelpCommand.class, HelpOptions.OPTIONS, "help", "displays help of given command"),
    new CommandMetaReference(DongleCheckCommand.class, DongleCheckOptions.OPTIONS,"dongle", "requests various controller and network information from dongle")
  };

  Map<String, CommandMetaReference> argToCommand = Stream.of(references)
      .collect(Collectors.toMap(CommandMetaReference::getCommandArgument, Function.identity()));

  static CommandMetaReference ofCommandArgument(String cliArgument) {
    return Optional.ofNullable(argToCommand.get(cliArgument))
        .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + cliArgument))
        ;
  }

  static CommandMetaReference[] commands() {
    return references;
  }

  void configure(String args[]) throws CommandOptionsException;
  void execute(CommandLine commandLine);
}
