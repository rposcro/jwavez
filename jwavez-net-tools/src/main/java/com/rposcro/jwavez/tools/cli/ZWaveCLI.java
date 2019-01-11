package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import java.util.stream.Stream;

public class ZWaveCLI {

  public static final String APP_CMD_NAME = "jwzt";

  public void processCommand(String... arguments) {
    try {
      System.out.println("JWaveZ Network Tool 1.0");
      Command command = Command.ofCommandArgument(arguments[0]).createCommand();
      command.configure(arguments);
      command.execute(null);
      System.out.println("");
    } catch(IndexOutOfBoundsException e) {
      printUsage();
    } catch(IllegalArgumentException e) {
      System.out.println("\nUnknown command " + arguments[0] + "\n");
      printUsage();
    } catch(CommandOptionsException e) {
      processCommand("help", arguments[0]);
    }
  }

  private void printUsage() {
    StringBuffer usage = new StringBuffer("usage: ").append(APP_CMD_NAME).append(" <command> [<options>]\n")
        .append("\nAvailable commands:\n");
    Stream.of(Command.commands())
        .forEach(cmd -> {
          usage.append(String.format("  %-12.12s  %s\n", cmd.getCommandArgument(), cmd.getDescription()));
        });
    usage.append("\nConsider help command on another command: ").append(APP_CMD_NAME).append(" help <command>");
    System.out.println(usage.toString());
  }

  public static void main(String[] args) {
    ZWaveCLI tool = new ZWaveCLI();
    tool.processCommand(args);
//    tool.processCommand("exclusion", "-d", "/dev/cu.usbmodem1421", "-t", "2000");
//    tool.processCommand("dongle", "-d", "/dev/cu.usbmodem1421");
//    tool.processCommand();
    System.exit(0);
  }
}
