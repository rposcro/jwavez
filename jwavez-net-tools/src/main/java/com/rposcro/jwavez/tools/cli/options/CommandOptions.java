package com.rposcro.jwavez.tools.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public interface CommandOptions {

  String OPT_DEVICE = "d";
  String OPT_TIMEOUT = "t";
  String OPT_NODE_ID = "n";

  static Options defaultDeviceBasedOptions() {
    return new Options()
        .addOption(Option.builder(OPT_DEVICE)
            .longOpt("dongleDevice")
            .optionalArg(true)
            .hasArg()
            .numberOfArgs(1)
            .argName("dongleDevice")
            .desc("controller dongle dongleDevice").build())
        ;
  }

  static Options defaultDeviceTimeoutBasedOptions() {
    return defaultDeviceBasedOptions()
        .addOption(Option.builder(OPT_TIMEOUT)
            .longOpt("timeout")
            .required(false)
            .hasArg()
            .numberOfArgs(1)
            .argName("millis")
            .type(Number.class)
            .desc("cancels transaction after this time").build())
        ;
  }

  static Options defaultNodeBasedOptions() {
    return defaultDeviceTimeoutBasedOptions()
        .addOption(Option.builder(OPT_NODE_ID)
            .longOpt("node")
            .required()
            .hasArg()
            .numberOfArgs(1)
            .argName("nodeId")
            .type(Number.class)
            .desc("id of node to be examined").build())
    ;
  }
}
