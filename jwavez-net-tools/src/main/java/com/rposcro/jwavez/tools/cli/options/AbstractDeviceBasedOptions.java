package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractDeviceBasedOptions implements CommandOptions {

  protected String device;
  protected CommandLine commandLine;

  protected AbstractDeviceBasedOptions(Options options, String[] args) throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      this.commandLine = parser.parse(options, args, false);
      if (commandLine.getArgs().length > 0) {
        throw new CommandOptionsException(String.format("Unrecognized tokens: '%s'", String.join(",", commandLine.getArgs())));
      }
      this.device = commandLine.getOptionValue(OPT_DEVICE);
    } catch(ParseException e) {
      throw new CommandOptionsException("Invalid option(s) format: " + e.getMessage(), e);
    }
  }

  public String getDevice() {
    return this.device;
  }
}
