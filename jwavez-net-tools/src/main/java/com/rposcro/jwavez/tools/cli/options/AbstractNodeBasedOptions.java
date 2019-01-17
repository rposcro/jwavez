package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractNodeBasedOptions extends AbstractDeviceTimeoutBasedOptions {

  private static final long DEFAULT_TIMEOUT = 10_000;

  protected NodeId nodeId;

  protected AbstractNodeBasedOptions(Options options, String[] args) throws CommandOptionsException {
    super(options, args);
    try {
      this.nodeId = new NodeId(parseByte(OPT_NODE_ID));
    } catch(ParseException e) {
      throw new CommandOptionsException(e.getMessage(), e);
    }
  }

  @Override
  public long getTimeout() {
    return getTimeout(DEFAULT_TIMEOUT);
  }

  public NodeId getNodeId() {
    return this.nodeId;
  }
}
