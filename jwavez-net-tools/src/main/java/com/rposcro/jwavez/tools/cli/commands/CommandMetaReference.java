package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.tools.cli.options.CommandOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.Options;

@Getter
@AllArgsConstructor
public class CommandMetaReference {

  private Class<? extends Command> command;
  private Options options;
  private String commandArgument;
  private String description;

  public Command createCommand() {
    try {
      return command.newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
