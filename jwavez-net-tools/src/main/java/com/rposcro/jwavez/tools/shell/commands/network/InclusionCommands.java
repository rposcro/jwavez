package com.rposcro.jwavez.tools.shell.commands.network;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.services.ConsoleAccessor;
import com.rposcro.jwavez.tools.shell.services.NetworkManagementService;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NETWORK)
public class InclusionCommands {

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

    @Command(name = "include", description = "Include new node into network",
        availabilityProvider = "dongleAvailability")
    public String includeNode(@Option(shortName = 't', longName = "timeout", defaultValue = "60") int timeout) throws SerialException {
        if (timeout > 60) {
            return "Maximum timeout value is 60 seconds";
        }

        console.flushLine("Entering node inclusion mode, cancel is not possible unless time is out: " + timeout + "[s]");
        Integer addedNodeId = networkManagementService.runInclusionMode(timeout * 1000);

        if (addedNodeId != null) {
            console.flushLine("Added new node into network: " + addedNodeId + "\n");
            console.flushLine("Fetching node information...\n");
            NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(addedNodeId);
            nodeInformationCache.cacheNodeInformation(nodeInformation);
            return "\nNode information:\n" + nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation);
        } else {
            return String.format("No node detected to include");
        }
    }

    @Command(name = "exclude", description = "Exclude node from network",
        availabilityProvider = "dongleAvailability")
    public String excludeNode(@Option(shortName = 't', longName = "timeout", defaultValue = "60") int timeout) throws SerialException {
        if (timeout > 60) {
            return "Maximum timeout value is 60 seconds";
        }

        console.flushLine("Entering node exclusion mode, cancel is not possible unless time is out: " + timeout + "[s]");
        Integer removedNodeId = networkManagementService.runExclusionMode(timeout * 1000);

        if (removedNodeId != null) {
            nodeInformationCache.removeNodeInformation(removedNodeId);
            return "Removed node from network: " + removedNodeId + "\n";
        } else {
            return String.format("No node detected to exclude");
        }
    }
}
