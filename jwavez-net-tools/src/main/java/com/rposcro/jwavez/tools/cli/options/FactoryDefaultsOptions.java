package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class FactoryDefaultsOptions extends AbstractDeviceTimeoutBasedOptions {

  private static final String OPT_CONFIRMATION = "iamsure";

  public static final Options OPTIONS = CommandOptions.defaultDeviceTimeoutBasedOptions()
      .addOption(Option.builder(OPT_CONFIRMATION)
          .required()
          .hasArg(false)
          .desc("needs to be given to confirm request is not accidental").build())
      ;

  public FactoryDefaultsOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
  }

  public boolean isConfirmed() {
    return commandLine.hasOption(OPT_CONFIRMATION);
  }
}
