package com.rposcro.jwavez.tools.cli.controller

import spock.lang.Specification

class CommandTreeNodeSpec extends Specification {

    def "find children"() {
        when:
        def treeNode = new CommandTreeNode("alias-1", "alias1 description")
                .addChild(new CommandTreeNode("sub-alias-1-1", "sub-alias-1-1 desription"))
                .addChild(new CommandTreeNode("sub-alias-1-2", "sub-alias-1-2 desription")
                        .addChild(new CommandTreeNode("sub-alias-1-2-1", "sub-alias-1-2-1 description")));

        then:
        treeNode.findChild("sub-alias-1-1") != null;
        treeNode.findChild("sub-alias-1-2") != null;
        treeNode.findChild("sub-alias-1-2").findChild("sub-alias-1-2-1") != null;
    }
}
