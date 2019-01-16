package com.rposcro.jwavez.tools.cli.commands

import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

class HelpCommandSpec extends Specification {

    @Unroll
    def "configuration succussful for #args"() {
        given:
        def command = new HelpCommand();

        when:
        command.configure(args.toArray(new String[0]));

        then:
        command.nodeUnderHelp.commandOtions.length == 0;
        command.nodeUnderHelp.commandNode.alias == alias;

        where:
        args                    | alias
        ["info"]                | "info"
        ["node"]                | "node"
        ["node", "association"] | "association"
    }

    @Unroll
    def "configuration fails with CommandOptionException for #args"() {
        given:
        def command = new HelpCommand();

        when:
        command.configure(args.toArray(new String[0]));

        then:
        CommandOptionsException ex = thrown(CommandOptionsException.class);
        println(ex.getMessage());

        where:
        args                    | _
        []                      | _
        ["-opt"]                | _
        ["arg"]                 | _
        ["info", "-opt"]        | _
        ["info", "arg"]         | _
    }
}
