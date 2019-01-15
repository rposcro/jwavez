package com.rposcro.jwavez.tools.cli.commands;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

@Getter
@AllArgsConstructor
public class CommandMetaReference {

  private Class<? extends Command> command;
  private Options options;
  private String commandArgument;
  private String description;
  private Supplier<String> helpSupplier;

  public CommandMetaReference(Class<? extends Command> command, Options options, String commandArgument, String description) {
    this.command = command;
    this.options = options;
    this.commandArgument = commandArgument;
    this.description = description;
    this.helpSupplier = this::defaultHelpMessage;
  }

  public Command createCommand() {
    try {
      return command.newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String helpMessage() {
    return this.helpSupplier.get();
  }

  private String defaultHelpMessage() {
    StringWriter strWriter = new StringWriter();
    strWriter.append("\n\nOptions:\n");
    PrintWriter writer = new PrintWriter(strWriter);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printOptions(writer, 80, this.getOptions(), 2, 1);
    return strWriter.getBuffer().toString();
  }
}
