package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;

public class DefaultDeviceTimeoutBasedOptions extends AbstractDeviceTimeoutBasedOptions {

    public static final Options OPTIONS = CommandOptions.defaultDeviceTimeoutBasedOptions();

    public DefaultDeviceTimeoutBasedOptions(String[] args) throws CommandOptionsException {
        super(OPTIONS, args);
    }
}
