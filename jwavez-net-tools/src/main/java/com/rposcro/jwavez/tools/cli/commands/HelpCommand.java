package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.controller.CommandHelpTool;
import com.rposcro.jwavez.tools.cli.controller.CommandLineContent;
import com.rposcro.jwavez.tools.cli.controller.CommandTree;
import com.rposcro.jwavez.tools.cli.controller.CommandsConfiguration;
import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;

public class HelpCommand implements Command {

  private CommandTree commandTree = CommandsConfiguration.defaultConfiguration().getCommandTree();
  private CommandHelpTool commandHelpTool = CommandsConfiguration.defaultConfiguration().getCommandHelpTool();

  private CommandLineContent helpNode;

  @Override
  public void configure(String... args) throws CommandOptionsException {
    if (args.length < 2) {
      throw new CommandOptionsException("Wrong arguments count");
    }

    try {
      helpNode = commandTree.scanCommandLine(Arrays.copyOfRange(args, 1, args.length));
    } catch(CommandLineException e) {
      throw new CommandOptionsException(e.getMessage());
    }
  }

  @Override
  public void execute(CommandLine commandLine) {
    StringBuffer buffer = commandHelpTool.buildCommandHelp(helpNode.getCommandNode());
    System.out.println(buffer.toString());
  }
}
