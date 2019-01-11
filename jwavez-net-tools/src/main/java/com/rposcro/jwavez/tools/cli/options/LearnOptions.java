package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class LearnOptions extends AbstractDeviceBasedOptions {

  private static final String OPT_DEVICE = "d";
  private static final String OPT_TIMEOUT = "t";
  private static final String OPT_SUMMARY = "s";

  public static final Options OPTIONS = new Options()
      .addOption(Option.builder(OPT_DEVICE).longOpt("device").hasArg().required().desc("controller dongle device").build())
      .addOption(Option.builder(OPT_TIMEOUT).longOpt("timeout").hasArg().argName("timeout").required(false).type(Number.class).desc("cancels node inclusion after this time").build())
      .addOption(Option.builder(OPT_SUMMARY).longOpt("summary").required(false).desc("upon successful inclusion, dongle's summary is printed").build())
      ;

  public LearnOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args, OPT_DEVICE, OPT_TIMEOUT);
  }

  public boolean showSummary() {
    return commandLine.hasOption(OPT_SUMMARY);
  }
}
