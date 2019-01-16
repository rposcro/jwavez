package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractDeviceTimeoutBasedOptions implements CommandOptions {

  private static final long DEFAULT_TIMEOUT = 0;

  protected String device;
  protected long timeout;
  protected CommandLine commandLine;

  protected AbstractDeviceTimeoutBasedOptions(Options options, String[] args) throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      this.commandLine = parser.parse(options, args, false);
      if (commandLine.getArgs().length > 0) {
        throw new CommandOptionsException(String.format("Unrecognized tokens: '%s'", String.join(",", commandLine.getArgs())));
      }
      this.device = commandLine.getOptionValue(OPT_DEVICE);
      this.timeout = commandLine.hasOption(OPT_TIMEOUT) ? parseLong(OPT_TIMEOUT) : -1;
    } catch(ParseException e) {
      throw new CommandOptionsException(e.getMessage(), e);
    }
  }

  public String getDevice() {
    return this.device;
  }

  public long getTimeout() {
    return getTimeout(DEFAULT_TIMEOUT);
  }

  public long getTimeout(long defaultTimeout) {
    return this.timeout < 0 ? defaultTimeout : this.timeout;
  }

  protected byte parseByte(String option) throws ParseException {
    return ((Number) commandLine.getParsedOptionValue(option)).byteValue();
  }

  protected int parseInteger(String option) throws ParseException {
    return ((Number) commandLine.getParsedOptionValue(option)).intValue();
  }

  protected long parseLong(String option) throws ParseException {
    return ((Number) commandLine.getParsedOptionValue(option)).longValue();
  }
}
