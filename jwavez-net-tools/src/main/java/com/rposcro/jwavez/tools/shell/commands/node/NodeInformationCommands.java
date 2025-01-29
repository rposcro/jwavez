package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@Command(group = CommandGroup.NODE)
public class NodeInformationCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Command(command = "learn", description = "Learn about node on network and select it")
    @CommandAvailability(provider = "dongleAvailability")
    public String fetchNodeInformation(@Option(longNames = "node-id", shortNames = 'n') int nodeId) throws SerialException {
        NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(nodeId);
        nodeInformationCache.cacheNodeInformation(nodeInformation);
        nodeScopeContext.setCurrentNodeId(nodeId);
        return "\n" + nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation);
    }
}
