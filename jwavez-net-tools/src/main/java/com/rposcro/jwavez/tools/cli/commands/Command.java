package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.IncludeNodeOptions;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.options.FactoryDefaultsOptions;
import com.rposcro.jwavez.tools.cli.options.HelpOptions;
import com.rposcro.jwavez.tools.cli.options.NetworkLearnOptions;
import com.rposcro.jwavez.tools.cli.options.ExcludeNodeOptions;
import com.rposcro.jwavez.tools.cli.options.NodeInfoOptions;
import com.rposcro.jwavez.tools.cli.options.SUCOptions;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;

public interface Command {

  CommandMetaReference[] references = {
    new CommandMetaReference(HelpCommand.class, HelpOptions.OPTIONS, "help", "displays help of given command"),
    new CommandMetaReference(DongleCheckCommand.class, DongleCheckOptions.OPTIONS,"info", "requests various controller and network information from dongle"),
    new CommandMetaReference(SUCCommand.class, SUCOptions.OPTIONS,"suc", "reads or sets SUC configuration on this dongle"),
    new CommandMetaReference(IncludeNodeCommand.class, IncludeNodeOptions.OPTIONS,"inclusion", "executes node inclusion process"),
    new CommandMetaReference(ExcludeNodeCommand.class, ExcludeNodeOptions.OPTIONS,"exclusion", "executes node exclusion process"),
    new CommandMetaReference(NetworkLearnCommand.class, NetworkLearnOptions.OPTIONS,"learn", "enables learn mode on this dongle, enables inclusion into another network"),
    new CommandMetaReference(FactoryDefaultsCommand.class, FactoryDefaultsOptions.OPTIONS,"purge", "resets dongle to factory defaults"),
    new CommandMetaReference(NodeInfoCommand.class, NodeInfoOptions.OPTIONS,"node", "requests information of node in network"),
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
