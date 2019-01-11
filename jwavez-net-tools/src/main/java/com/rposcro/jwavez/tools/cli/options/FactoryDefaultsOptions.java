package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FactoryDefaultsOptions implements CommandOptions {

  private static final String OPT_DEVICE = "d";
  private static final String OPT_TIMEOUT = "t";
  private static final String OPT_CONFIRMATION = "iamsure";

  public static final Options OPTIONS = new Options()
      .addOption(Option.builder(OPT_DEVICE).longOpt("device").hasArg().required().desc("controller dongle device").build())
      .addOption(Option.builder(OPT_TIMEOUT).longOpt("timeout").hasArg().required(false).type(Number.class).desc("cancels node inclusion after this time").build())
      .addOption(Option.builder(OPT_CONFIRMATION).hasArg(false).required().desc("needs to be given to confirm request is not accidential").build())
      ;

  private CommandLine commandLine;
  private long timeout;

  public FactoryDefaultsOptions(String[] args) throws CommandOptionsException {
    try {
      CommandLineParser parser = new DefaultParser();
      commandLine = parser.parse(OPTIONS, args, false);
      timeout = commandLine.hasOption(OPT_TIMEOUT) ? ((Number) commandLine.getParsedOptionValue(OPT_TIMEOUT)).intValue() : 0;
    } catch(ParseException e) {
      throw new CommandOptionsException(e);
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
