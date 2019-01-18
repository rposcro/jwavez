package com.rposcro.jwavez.tools.cli.options.node;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.CommandOptions;
import com.rposcro.jwavez.tools.cli.options.node.AbstractNodeBasedOptions;
import org.apache.commons.cli.Options;

public class DefaultNodeBasedOptions extends AbstractNodeBasedOptions {

  public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions();

  public DefaultNodeBasedOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
  }
}
