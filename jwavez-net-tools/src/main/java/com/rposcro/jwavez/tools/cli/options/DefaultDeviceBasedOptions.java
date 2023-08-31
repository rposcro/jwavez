package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;

public class DefaultDeviceBasedOptions extends AbstractDeviceBasedOptions {

    public static final Options OPTIONS = CommandOptions.defaultDeviceBasedOptions();

    public DefaultDeviceBasedOptions(String[] args) throws CommandOptionsException {
        super(OPTIONS, args);
    }
}
