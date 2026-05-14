package com.rposcro.jwavez.tools.shell.commands.scope;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.ScopeSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@CommandGroup(name = com.rposcro.jwavez.tools.shell.commands.CommandGroup.TOP)
public class TopScopeCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private ScopeSwitchService scopeSwitchService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Command(name = "dongle", description = "Change working scope to Dongle")
    public String switchToDongleScope() {
        return switchToTopScope(ShellScope.DONGLE);
    }

    @Command(name = "network", description = "Change working scope to Network")
    public String switchToNetworkScope() {
        return switchToTopScope(ShellScope.NETWORK);
    }

    @Command(name = "talk", description = "Change working scope to Talk")
    public String switchToTalkScope() {
        return switchToTopScope(ShellScope.TALK);
    }

    @Command(name = "node", description = "Change working scope to Node")
    public String switchToNodeScope(@Option(shortName = 'n', longName = "node-id", defaultValue = "null") Integer nodeId) {
        switchToTopScope(ShellScope.NODE);
        String message = "Scope changed to " + ShellScope.NODE;
        if (nodeId != null && nodeInformationCache.isNodeKnown(nodeId)) {
            nodeScopeContext.setCurrentNodeId(nodeId);
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
            message += "\nNode selection changed\n" + nodeInformationFormatter.formatShortNodeInfo(nodeInformation) + "\n";
        }
        return message;
    }

    private String switchToTopScope(ShellScope requestedScope) {
        scopeSwitchService.switchScope(requestedScope);
        return "Scope changed to " + shellContext.getShellScope();
    }
}
