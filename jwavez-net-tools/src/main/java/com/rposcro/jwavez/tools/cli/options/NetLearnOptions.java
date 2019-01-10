package com.rposcro.jwavez.tools.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class NetLearnOptions implements CommandOptions {

  private static final Options options = new Options()
      .addOption(new Option("d", "device", true, "controller dongle device"))
      .addOption(new Option("t", "timeout", false, "cancels network learn state after this time"))
      ;

  public Options options() {
    return options;
  }
}
