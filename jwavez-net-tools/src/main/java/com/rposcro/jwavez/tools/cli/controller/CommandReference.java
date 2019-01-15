package com.rposcro.jwavez.tools.cli.controller;

import com.rposcro.jwavez.tools.cli.commands.Command;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

@Getter
@AllArgsConstructor
public class CommandReference {

  private Class<? extends Command> commandClass;
  private Options optionsDefinition;

  public Command createCommand() {
    try {
      return commandClass.newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String defaultHelpMessage() {
    StringWriter strWriter = new StringWriter();
    strWriter.append("\n\nOptions:\n");
    PrintWriter writer = new PrintWriter(strWriter);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printOptions(writer, 80, this.getOptionsDefinition(), 2, 1);
    return strWriter.getBuffer().toString();
  }
}
