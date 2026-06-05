package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.formatters.NodeInformationFormatter;
import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.services.NodeInformationCache;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
public class NodeInformationCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeInformationCache nodeInformationCache;

    @Autowired
    private NodeInformationService nodeInformationService;

    @Autowired
    private NodeInformationFormatter nodeInformationFormatter;

    @Command(name = "learn", description = "Learn about node on network and select it", availabilityProvider = "dongleAvailability")
    public String fetchNodeInformation(@Argument(index = 0, description = "Node id on network to learn about") int nodeId) throws SerialException {
        NodeInformation nodeInformation = nodeInformationService.fetchNodeInformation(nodeId);
        nodeInformationCache.cacheNodeInformation(nodeInformation);
        nodeScopeContext.setCurrentNodeId(nodeId);
        return "\n" + nodeInformationFormatter.formatVerboseNodeInfo(nodeInformation);
    }
}
