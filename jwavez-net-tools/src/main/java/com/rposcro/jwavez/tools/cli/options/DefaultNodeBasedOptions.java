package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;

public class DefaultNodeBasedOptions extends AbstractNodeBasedOptions {

  public static final Options OPTIONS = CommandOptions.defaultNodeBasedOptions();

  public DefaultNodeBasedOptions(String[] args) throws CommandOptionsException {
    super(OPTIONS, args);
  }
}
