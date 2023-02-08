package com.rposcro.jwavez.tools.cli.options.node

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions
import spock.lang.Specification
import spock.lang.Unroll

class DefaultNodeBasedOptionsSpec extends Specification {

    @Unroll
    def "parse success for #args"() {
        when:
        def options = new DefaultNodeBasedOptions(args.toArray(new String[0]));

        then:
        options.device == expDevice;
        options.timeout == expTimeout;
        options.nodeId == expNodeId;

        where:
        args                                           | expDevice    | expTimeout | expNodeId
        ["-d", "/dev/dummy", "-n", "12"]               | "/dev/dummy" | 0          | 12
        ["-d", "/dev/dummy", "-t", "1234", "-n", "55"] | "/dev/dummy" | 1234       | 55
    }

    @Unroll
    def "parse failure for #args"() {
        when:
        def options = new DefaultNodeBasedOptions(args.toArray(new String[0]));

        then:
        CommandOptionsException ex = thrown();
        println(ex.getMessage());

        where:
        args                                        | _
        []                                          | _
        ["-d", "/dev/dummy"]                        | _
        ["-d", "/dev/dummy", "-t", "1234"]          | _
        ["-d", "/dev/dummy", "-n", "numer"]         | _
        ["-d", "/dev/dummy", "-n", "33", "oddArg"]  | _
        ["-d", "/dev/dummy", "-n", "33", "-oddOpt"] | _
    }
}
