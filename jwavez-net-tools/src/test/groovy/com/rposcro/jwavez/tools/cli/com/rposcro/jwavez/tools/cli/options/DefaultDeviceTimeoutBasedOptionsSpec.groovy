package com.rposcro.jwavez.tools.cli.com.rposcro.jwavez.tools.cli.options

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException
import com.rposcro.jwavez.tools.cli.options.DefaultDeviceTimeoutBasedOptions
import spock.lang.Specification
import spock.lang.Unroll

class DefaultDeviceTimeoutBasedOptionsSpec extends Specification {

    @Unroll
    def "parse success for #args"() {
        when:
        def options = new DefaultDeviceTimeoutBasedOptions(args.toArray(new String[0]));

        then:
        options.device == expDevice;
        options.timeout == expTimeout;

        where:
        args                                | expDevice     | expTimeout
        ["-d", "/dev/dummy"]                | "/dev/dummy"  | 0
        ["-d", "/dev/dummy", "-t", "1234"]  | "/dev/dummy"  | 1234
    }

    @Unroll
    def "parse failure for #args"() {
        when:
        def options = new DefaultDeviceTimeoutBasedOptions(args.toArray(new String[0]));

        then:
        CommandOptionsException ex = thrown();
        println(ex.getMessage());

        where:
        args                                | _
        []                                  | _
        ["-t", "1234"]                      | _
        ["-d"]                              | _
        ["-d", "-t", "1234"]                | _
        ["-d", "/dev/dummy", "-t"]          | _
        ["-d", "/dev/dummy", "-t", "numer"] | _
        ["-d", "/dev/dummy", "oddArg"]      | _
        ["-d", "/dev/dummy", "-oddOpt"]     | _
        ["oddArg", "-d", "/dev/dummy"]      | _
    }
}
