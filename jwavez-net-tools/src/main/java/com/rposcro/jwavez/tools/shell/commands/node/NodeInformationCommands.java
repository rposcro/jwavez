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
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.NODE)
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

    @ShellMethod(value = "Learn about node on network and select it", key = { "learn" })
    public String fetchNodeInformation(@ShellOption(value = { "--node-id", "-id" }) int nodeId) throws SerialException {
        NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(nodeId);
        nodeInformationCache.cacheNodeInformation(nodeInformation);
        nodeScopeContext.setCurrentNodeId(nodeId);
        return "\n" + nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation);
    }

    @ShellMethodAvailability(value = { "learn" })
    public Availability checkRemoteAvailability() {
        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
