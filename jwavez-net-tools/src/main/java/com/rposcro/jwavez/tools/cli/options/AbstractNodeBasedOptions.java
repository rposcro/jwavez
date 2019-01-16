package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractNodeBasedOptions extends AbstractDeviceTimeoutBasedOptions {

  protected byte nodeId;

  protected AbstractNodeBasedOptions(Options options, String[] args) throws CommandOptionsException {
    super(options, args);
    try {
      this.nodeId = parseByte(OPT_NODE_ID);
    } catch(ParseException e) {
      throw new CommandOptionsException(e.getMessage(), e);
    }
  }

  public byte getNodeId() {
    return this.nodeId;
  }
}
