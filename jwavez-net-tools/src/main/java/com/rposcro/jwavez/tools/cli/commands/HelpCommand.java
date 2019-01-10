package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

public class HelpCommand implements Command {

  private CommandMetaReference commandReference;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    if (args.length != 2) {
      throw new CommandOptionsException("Wrong arguments count");
    }

    try {
      commandReference = Command.ofCommandArgument(args[1]);
    } catch(IllegalArgumentException e) {
      throw new CommandOptionsException(e.getMessage());
    }
  }

  @Override
  public void execute(CommandLine commandLine) {
    StringWriter strWriter = new StringWriter();
    strWriter.append("JWaveZ Tools\n")
        .append("Help on command: " + commandReference.getCommandArgument() + "\n")
        .append(commandReference.getDescription())
        .append("\n\nOptions:\n");
    PrintWriter writer = new PrintWriter(strWriter);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printOptions(writer, 80, commandReference.getOptions(), 2, 1);
    System.out.println(strWriter.getBuffer().toString());
  }
}
