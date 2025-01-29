package com.rposcro.jwavez.tools.shell.commands.top;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.ScopeSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@Command(command = CommandGroup.TOP)
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

    @Command(command = "dongle", description = "Change working scope to Dongle")
    public String switchToDongleScope() {
        return switchToScope(ShellScope.DONGLE);
    }

    @Command(command = "network", description = "Change working scope to Network")
    public String switchToNetworkScope() {
        return switchToScope(ShellScope.NETWORK);
    }

    @Command(command = "talk", description = "Change working scope to Talk")
    public String switchToTalkScope() {
        return switchToScope(ShellScope.TALK);
    }

    @Command(command = "node", description = "Change working scope to Node")
    public String switchToNodeScope(@Option(longNames = "node-id", shortNames = 'n') Integer nodeId) {
        switchToScope(ShellScope.NODE);
        String message = "Scope changed to " + ShellScope.NODE;
        if (nodeId != null && nodeInformationCache.isNodeKnown(nodeId)) {
            nodeScopeContext.setCurrentNodeId(nodeId);
            NodeInformation nodeInformation = nodeInformationCache.getNodeDetails(nodeId);
            message += "\nNode selection changed\n" + nodeInformationFormatter.formatShortNodeInfo(nodeInformation) + "\n";
        }
        return message;
    }

    private String switchToScope(ShellScope requestedScope) {
        if (!shellContext.getShellScope().hasChild(requestedScope)) {
            throw new IllegalArgumentException("Scope " + requestedScope + " is not a child of " + shellContext.getShellScope());
        }
        scopeSwitchService.switchScope(requestedScope);
        return "Scope changed to " + shellContext.getShellScope();
    }
}
