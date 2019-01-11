package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class AddNodeOptions extends AbstractDeviceBasedOptions {

  private static final String OPT_DEVICE = "d";
  private static final String OPT_TIMEOUT = "t";

  public static final Options OPTIONS = new Options()
      .addOption(Option.builder(OPT_DEVICE).longOpt("device").hasArg().required().desc("controller dongle device").build())
      .addOption(Option.builder(OPT_TIMEOUT).longOpt("timeout").hasArg().required(false).type(Number.class).desc("cancels node inclusion after this time").build())
      ;

  public AddNodeOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args, OPT_DEVICE, OPT_TIMEOUT);
  }
}
