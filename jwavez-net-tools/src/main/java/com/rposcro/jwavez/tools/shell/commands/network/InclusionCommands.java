package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkManagementService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.NETWORK)
public class InclusionCommands {

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private NetworkManagementService networkManagementService;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Autowired
    private ConsoleAccessor console;

    @ShellMethod(value = "Include new node into network", key="include")
    public String includeNode(@ShellOption(value = { "--timeout", "-to" }, defaultValue = "60") int timeout) throws SerialException {
        if (timeout > 60) {
            return "Maximum timeout value is 60 seconds";
        }

        console.flushLine("Entering node inclusion mode, cancel is not possible unless time is out: " + timeout + "[s]");
        Integer addedNodeId = networkManagementService.runInclusionMode(timeout * 1000);

        if (addedNodeId != null) {
            console.flushLine("Added new node into network: " + addedNodeId + "\n");
            console.flushLine("Fetching node information...\n");
            NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(addedNodeId);
            nodeInformationCache.persist();
            return "\nNode information:\n" + nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation);
        } else {
            return String.format("No node detected to include");
        }
    }

    @ShellMethod(value = "Exclude node from network", key="exclude")
    public String excludeNode(@ShellOption(value = { "--timeout", "-to" }, defaultValue = "60") int timeout) throws SerialException {
        if (timeout > 60) {
            return "Maximum timeout value is 60 seconds";
        }

        console.flushLine("Entering node exclusion mode, cancel is not possible unless time is out: " + timeout + "[s]");
        Integer removedNodeId = networkManagementService.runExclusionMode(timeout * 1000);

        if (removedNodeId != null) {
            nodeInformationCache.removeNodeDetails(removedNodeId);
            nodeInformationCache.persist();
            return "Removed node from network: " + removedNodeId + "\n";
        } else {
            return String.format("No node detected to exclude");
        }
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.NETWORK != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDevice() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }
}
