package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FactoryDefaultsOptions implements CommandOptions {

  private static final String OPT_CONFIRMATION = "iamsure";

  public static final Options OPTIONS = CommandOptions.defaultDeviceTimeoutBasedOptions()
      .addOption(Option.builder(OPT_CONFIRMATION)
          .required()
          .hasArg(false)
          .desc("needs to be given to confirm request is not accidential").build())
      ;

  private CommandLine commandLine;
  private long timeout;

  public FactoryDefaultsOptions(String[] args) throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      commandLine = parser.parse(OPTIONS, args, false);
      timeout = commandLine.hasOption(OPT_TIMEOUT) ? ((Number) commandLine.getParsedOptionValue(OPT_TIMEOUT)).intValue() : 0;
    } catch(ParseException e) {
      throw new CommandOptionsException(e.getMessage(), e);
    }
  }

  public String getDevice() {
    return commandLine.getOptionValue(OPT_DEVICE);
  }

  public long getTimeout() {
    return timeout;
  }

  public boolean isConfirmed() {
    return commandLine.hasOption(OPT_CONFIRMATION);
  }
}
