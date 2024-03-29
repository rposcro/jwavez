package com.rposcro.jwavez.tools.cli.controller

import com.rposcro.jwavez.tools.cli.exceptions.CommandLineException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CommandTreeSpec extends Specification {

    @Shared
    def commandTree;

    def setup() {
        commandTree = CommandTree.builder()
                .rootNode(new CommandTreeNode("", "")
                        .addChild(new CommandTreeNode("alias-1", "alias-1 description")
                                .addChild(new CommandTreeNode("alias-1-1", "sub-alias-1-1 desription"))
                                .addChild(new CommandTreeNode("alias-1-2", "sub-alias-1-2 desription")
                                        .addChild(new CommandTreeNode("alias-1-2-1", "sub-alias-1-2-1 description"))))
                ).build();
    }

    @Unroll
    def "scans command line successfully"() {
        when:
        def content = commandTree.scanCommandLine(args.toArray(new String[0]));

        then:
        content.commandNode.alias == alias;

        where:
        args                                            | alias         | options
        ["alias-1"]                                     | "alias-1"     | []
        ["alias-1", "-o1", "-o2"]                       | "alias-1"     | ["-o1", "-o2"]
        ["alias-1", "alias-1-1"]                        | "alias-1-1"   | []
        ["alias-1", "alias-1-2", "alias-1-2-1", "-o3"]  | "alias-1-2-1" | ["-o3"]
        ["alias-1", "arg0"]                             | "alias-1"     | ["arg0"]
        ["alias-1", "alias-1-2", "arg0", "arg1", "-o3"] | "alias-1-2"   | ["arg0", "arg1", "-o3"]
    }

    @Unroll
    def "scans command line fails"() {
        when:
        commandTree.scanCommandLine(args.toArray(new String[0]));

        then:
        thrown CommandLineException;

        where:
        args                        | _
        []                          | _
        ["alias-1-2-1"]             | _
        ["alias-1-1", "-o1", "-o2"] | _
    }

    @Unroll
    def "find command node successfully"() {
        when:
        def commandNode = commandTree.findClosestMatchingCommandNode(path);

        then:
        commandNode.alias == alias;

        where:
        path                                    | alias
        ["alias-1"]                             | "alias-1"
        ["alias-1", "alias-1-1"]                | "alias-1-1"
        ["alias-1", "alias-1-2"]                | "alias-1-2"
        ["alias-1", "alias-1-2", "alias-1-2-1"] | "alias-1-2-1"
    }

    @Unroll
    def "find command node fails"() {
        when:
        def commandNode = commandTree.findClosestMatchingCommandNode(path);

        then:
        thrown CommandLineException;

        where:
        path                         | _
        []                           | _
        ["alias-ne"]                 | _
        ["alias-1-1"]                | _
        ["alias-1-2"]                | _
        ["alias-1-2-1"]              | _
        ["alias-1-2", "alias-1-2-1"] | _
    }

    @Unroll
    def "extracts command path"() {
        given:
        def commandTree = new CommandTree();

        when:
        def commandPath = commandTree.extractCommandPath(args.toArray(new String[0]));

        then:
        commandPath.size() == expectedPath.size();
        commandPath == expectedPath;

        where:
        args                     | expectedPath
        []                       | []
        ["-opt", "-arg"]         | []
        ["item"]                 | ["item"]
        ["item", "thing"]        | ["item", "thing"]
        ["item", "thing"]        | ["item", "thing"]
        ["item", "thing", "-o1"] | ["item", "thing"]
        ["item", "-o1", "-o2"]   | ["item"]
        ["item", "-o1", "thing"] | ["item"]
    }
}
