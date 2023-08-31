package com.rposcro.jwavez.tools.cli.options;

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractDeviceTimeoutBasedOptions extends AbstractDeviceBasedOptions {

    private static final long DEFAULT_TIMEOUT = 0;

    protected long timeout;

    protected AbstractDeviceTimeoutBasedOptions(Options options, String[] args) throws CommandOptionsException {
        super(options, args);
        try {
            this.timeout = commandLine.hasOption(OPT_TIMEOUT) ? parseLong(OPT_TIMEOUT) : -1;
        } catch (ParseException e) {
            throw new CommandOptionsException("Invalid option(s) format: " + e.getMessage(), e);
        }
    }

    public long getTimeout() {
        return getTimeout(DEFAULT_TIMEOUT);
    }

    public long getTimeout(long defaultTimeout) {
        return this.timeout < 0 ? defaultTimeout : this.timeout;
    }

    protected byte parseByte(String option) throws ParseException {
        return ((Number) commandLine.getParsedOptionValue(option)).byteValue();
    }

    protected int parseInteger(String option) throws ParseException {
        return ((Number) commandLine.getParsedOptionValue(option)).intValue();
    }

    protected long parseLong(String option) throws ParseException {
        return ((Number) commandLine.getParsedOptionValue(option)).longValue();
    }
}
