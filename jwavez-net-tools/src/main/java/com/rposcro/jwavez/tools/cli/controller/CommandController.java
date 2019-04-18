package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandController {

  private static final int EXIT_CODE_SUCCESSFUL = 0;
  private static final int EXIT_CODE_GENERAL_ERROR = 1;

  private CommandTree commandTree = CommandsConfiguration.defaultConfiguration().getCommandTree();
  private CommandUsageTool commandUsageTool = CommandsConfiguration.defaultConfiguration().getCommandUsageTool();

  public int executeCommand(String... args) {
    if (args.length < 1) {
      printUsage();
      return EXIT_CODE_SUCCESSFUL;
    } else {
      return processCommand(args);
    }
  }

  private int processCommand(String... args) {
    try {
      CommandLineContent content = commandTree.scanCommandLine(args);
      CommandTreeNode commandNode = content.getCommandNode();
      CommandReference reference = commandNode.getCommandReference().orElseThrow(
          () -> new CommandOptionsException("Not complete or incorrect command: " + commandNode.pathAsString()));
      invokeCommand(reference, content.getCommandOtions());
      return EXIT_CODE_SUCCESSFUL;
    } catch(CommandLineException e) {
      System.out.println("Incorrect command line\n");
      log.warn("", e.getCause());
      printUsage();
      return EXIT_CODE_GENERAL_ERROR;
    } catch(CommandOptionsException | CommandExecutionException e) {
      System.out.println(e.getMessage() + "\n");
      log.warn("", e.getCause());
      return EXIT_CODE_GENERAL_ERROR;
    } catch(SerialException e) {
      System.out.printf("Serial communication error: %s\n\n", e.getMessage());
      log.warn("", e.getCause());
      return EXIT_CODE_GENERAL_ERROR;
    }
  }

  private void invokeCommand(CommandReference reference, String[] options)
      throws CommandOptionsException, CommandExecutionException, SerialException {
    try ( Command command = reference.createCommand() ) {
      command.configure(options);
      command.execute();
    }
  }

  private void printUsage() {
    System.out.println(commandUsageTool.buildGlobalUsage().toString());
  }
}
