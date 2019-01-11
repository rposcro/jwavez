package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractDeviceBasedOptions {

  protected String device;
  protected long timeout;
  protected CommandLine commandLine;

  protected AbstractDeviceBasedOptions(Options options, String[] args, String deviceOption) throws CommandOptionsException {
    this(options, args, deviceOption, null);
  }

  protected AbstractDeviceBasedOptions(Options options, String[] args, String deviceOption, String timeoutOption)
      throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      this.commandLine = parser.parse(options, args, false);
      this.device = commandLine.getOptionValue(deviceOption);
      if (timeoutOption != null) {
        timeout = commandLine.hasOption(timeoutOption) ? ((Number) commandLine.getParsedOptionValue(timeoutOption)).longValue() : 0;
      }
    } catch(ParseException e) {
      throw new CommandOptionsException(e);
    }
  }

  public String getDevice() {
    return this.device;
  }

  public long getTimeout() {
    return this.timeout;
  }
}
