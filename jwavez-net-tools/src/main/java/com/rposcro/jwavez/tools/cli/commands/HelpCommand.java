package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.controller.CommandHelpTool;
import com.rposcro.jwavez.tools.cli.controller.CommandLineContent;
import com.rposcro.jwavez.tools.cli.controller.CommandTree;
import com.rposcro.jwavez.tools.cli.controller.CommandsConfiguration;
import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;

public class HelpCommand implements Command {

  private CommandTree commandTree = CommandsConfiguration.defaultConfiguration().getCommandTree();
  private CommandHelpTool commandHelpTool = CommandsConfiguration.defaultConfiguration().getCommandHelpTool();

  private CommandLineContent nodeUnderHelp;

  @Override
  public void configure(String... args) throws CommandOptionsException {
    if (args.length < 1) {
      throw new CommandOptionsException("Wrong arguments count");
    }

    try {
      nodeUnderHelp = commandTree.scanCommandLine(args);
      if (nodeUnderHelp.getCommandOtions().length > 0) {
        throw new CommandOptionsException(String.format("Cannot display help on '%s'", String.join(" ", args)));
      }
    } catch(CommandLineException e) {
      throw new CommandOptionsException(String.format("Cannot display help on '%s', cause: %s", String.join(" ", args), e.getMessage()));
    }
  }

  @Override
  public void execute() {
    StringBuffer buffer = commandHelpTool.buildCommandHelp(nodeUnderHelp.getCommandNode());
    System.out.println(buffer.toString());
  }
}
