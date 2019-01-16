package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandController {

  private CommandTree commandTree = CommandsConfiguration.defaultConfiguration().getCommandTree();
  private CommandUsageTool commandUsageTool = CommandsConfiguration.defaultConfiguration().getCommandUsageTool();

  public void executeCommand(String... args) {
    if (args.length < 1) {
      printUsage();
    } else {
      processCommand(args);
    }
  }

  private void processCommand(String... args) {
    try {
      CommandLineContent content = commandTree.scanCommandLine(args);
      CommandTreeNode commandNode = content.getCommandNode();
      CommandReference reference = commandNode.getCommandReference().orElseThrow(
          () -> new CommandOptionsException("Not complete or incorrect command: " + commandNode.pathAsString()));
      invokeCommand(reference, content.getCommandOtions());
    } catch(CommandLineException e) {
      System.out.println("Incorrect command line\n");
      log.warn("", e.getCause());
      printUsage();
    } catch(CommandOptionsException e) {
      System.out.println(e.getMessage() + "\n");
      log.warn("", e.getCause());
    }
  }

  private void invokeCommand(CommandReference reference, String[] options) throws CommandOptionsException {
    Command command = reference.createCommand();
    command.configure(options);
    command.execute();
  }

  private void printUsage() {
    System.out.println(commandUsageTool.buildGlobalUsage().toString());
  }
}
