package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import java.util.stream.Stream;

public class ZWaveCLI {

  public static final String APP_NAME = "jwzt";

  public void processCommand(String... arguments) {
    try {
      Command command = Command.ofCommandArgument(arguments[0]).createCommand();
      command.configure(arguments);
      command.execute(null);
    } catch(IndexOutOfBoundsException e) {
      printUsage();
    } catch(CommandOptionsException e) {
      processCommand("help", arguments[0]);
    }
  }

  private void printUsage() {
    StringBuffer usage = new StringBuffer("usage: ").append(APP_NAME).append("<command> [<options>]\n")
        .append("\nAvailable commands:\n");
    Stream.of(Command.commands())
        .forEach(cmd -> {
          usage.append(String.format("  %-10.10s  %s\n", cmd.getCommandArgument(), cmd.getDescription()));
        });
    usage.append("\nConsider help command on another command: ").append(APP_NAME).append(" help <command>");
    System.out.println(usage.toString());
  }

  public static void main(String[] args) {
    ZWaveCLI tool = new ZWaveCLI();
    tool.processCommand(args);
//    tool.processCommand("dongle", "-d", "/dev/cu.usbmodem1421");
    System.exit(0);
  }
}
