package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import lombok.Getter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class NodeInfoOptions extends AbstractDeviceBasedOptions {

  private static final String OPT_DEVICE = "d";
  private static final String OPT_TIMEOUT = "t";
  private static final String OPT_NODE_ID = "n";

  public static final Options OPTIONS = new Options()
      .addOption(Option.builder(OPT_DEVICE).longOpt("device").hasArg().argName("device").required().desc("controller dongle device").build())
      .addOption(Option.builder(OPT_TIMEOUT).longOpt("timeout").hasArg().argName("milliseconds").required(false).type(Number.class).desc("cancels node inclusion after this time").build())
      .addOption(Option.builder(OPT_NODE_ID).longOpt("node").hasArg().argName("nodeId").required().type(Number.class).desc("id of node to be examined").build())
      ;

  @Getter
  private byte nodeId;

  public NodeInfoOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args, OPT_DEVICE, OPT_TIMEOUT);
    try {
      nodeId = ((Number) commandLine.getParsedOptionValue(OPT_NODE_ID)).byteValue();
    } catch(ParseException e) {
      throw new CommandOptionsException(e);
    }
  }
}
