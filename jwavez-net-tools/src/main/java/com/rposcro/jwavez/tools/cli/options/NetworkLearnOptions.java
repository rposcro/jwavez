package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class NetworkLearnOptions extends AbstractDeviceTimeoutBasedOptions {

  private static final String OPT_SUMMARY = "s";

  public static final Options OPTIONS = CommandOptions.defaultDeviceBasedOptions()
      .addOption(Option.builder(OPT_SUMMARY)
          .longOpt("summary")
          .required(false)
          .desc("upon successful inclusion, dongle's summary is printed").build())
      ;

  public NetworkLearnOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
  }

  public boolean showSummary() {
    return commandLine.hasOption(OPT_SUMMARY);
  }
}
