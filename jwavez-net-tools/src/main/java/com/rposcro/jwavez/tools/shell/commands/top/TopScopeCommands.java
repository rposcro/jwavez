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
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.TOP)
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

    @ShellMethod(value = "Change working scope to Dongle", key = "dongle")
    public String switchToDongleScope() {
        return switchToScope(ShellScope.DONGLE);
    }

    @ShellMethod(value = "Change working scope to Network", key = "network")
    public String switchToNetworkScope() {
        return switchToScope(ShellScope.NETWORK);
    }

    @ShellMethod(value = "Change working scope to Talk", key = "talk")
    public String switchToTalkScope() {
        return switchToScope(ShellScope.TALK);
    }

    @ShellMethod(value = "Change working scope to Node", key = "node")
    public String switchToNodeScope(@ShellOption(value = {"--node-id", "-id"}, defaultValue = ShellOption.NULL) Integer nodeId) {
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
