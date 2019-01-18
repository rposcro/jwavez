package com.rposcro.jwavez.tools.cli.options.node

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException
import com.rposcro.jwavez.tools.cli.options.node.AbstractNodeBasedOptions
import com.rposcro.jwavez.tools.cli.options.node.NodeConfigurationReadOptions
import spock.lang.Specification
import spock.lang.Unroll

class NodeConfigurationReadOptionsSpec extends Specification {

    @Unroll
    def "successful single parse for #args"() {
        when:
        def options = new NodeConfigurationReadOptions(args.split(" "));

        then:
        options.device == expDevice;
        options.timeout == expTimeout;
        (options.nodeId.getId() & 0xff) == expNode;
        options.getParameterNumbers() == expNums;

        where:
        args                                     | expNums | expDevice | expNode |expTimeout
        "-pn 12 -d /dev/a -n 3 -pn 12"           | [12]    | "/dev/a"  | 3       | AbstractNodeBasedOptions.DEFAULT_TIMEOUT
        "-pn 13 -t 667 -d /dev/b -n 189 -pn 13"  | [13]    | "/dev/b"  | 189     | 667
    }

    @Unroll
    def "successful range parse for #args"() {
        when:
        def options = new NodeConfigurationReadOptions(args.split(" "));

        then:
        options.getParameterNumbers() == expNums;

        where:
        args                              | expNums
        "-d /dev/a -n 3 -pn 12-12"        | [12]
        "-d /dev/a -n 3 -pn 12-16"        | [12, 13, 14, 15, 16]
        "-d /dev/a -n 3 -pn 17-11"        | [17, 16, 15, 14, 13, 12, 11]
    }

    @Unroll
    def "successful list parse for #args"() {
        when:
        def options = new NodeConfigurationReadOptions(args.split(" "));

        then:
        options.getParameterNumbers() == expNums;

        where:
        args                              | expNums
        "-d /dev/a -n 3 -pn 12,12"        | [12, 12]
        "-d /dev/a -n 3 -pn 12,16"        | [12, 16]
        "-d /dev/a -n 3 -pn 20,16"        | [20, 16]
        "-d /dev/a -n 3 -pn 20,16,4,200"  | [20, 16, 4, 200]
    }

    @Unroll
    def "parse failure for #args"() {
        when:
        new NodeConfigurationReadOptions(args.split(" "));

        then:
        CommandOptionsException ex = thrown();
        println(ex.getMessage());

        where:
        args                              | _
        ""                                | _
        " "                               | _
        "-d /dev"                         | _
        "-n 56"                           | _
        "-pn 77"                          | _
        "-t 12"                           | _
        "-d /dev -n 56"                   | _
        "-d /dev -pn 77"                  | _
        "-d /dev -t 12"                   | _
        "-n 56 -pn 77"                    | _
        "-n 56 -t 12"                     | _
        "-pn 77 -t 12"                    | _
        "-d /dev -n 56 -t 12"             | _
        "-n 56 -pn 77 -t 12"              | _

        "-d /dev -n xxx -pn 77"           | _
        "-d /dev -n 56 -pn xxx"           | _

        "-d /dev -n 56 -pn 77-"           | _
        "-d /dev -n 56 -pn -"             | _
        "-d /dev -n 56 -pn -77"           | _
        "-d /dev -n 56 -pn 77-j"          | _
        "-d /dev -n 56 -pn j-77"          | _
        "-d /dev -n 56 -pn j-k"           | _

        "-d /dev -n 56 -pn 77,"           | _
        "-d /dev -n 56 -pn ,77"           | _
        "-d /dev -n 56 -pn ,77,55"        | _
        "-d /dev -n 56 -pn 77,55,"        | _
        "-d /dev -n 56 -pn j,55"          | _
        "-d /dev -n 56 -pn 77,j"          | _
        "-d /dev -n 56 -pn j,k"           | _
    }}
