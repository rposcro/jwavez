package com.rposcro.jwavez.tools.shell.commands.node;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.models.NodeMultiChannelInformation;
import com.rposcro.jwavez.tools.shell.scopes.NodeScopeContext;
import com.rposcro.jwavez.tools.shell.services.NodeMultiChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;

import java.util.Arrays;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.NODE)
public class NodeMultiChannelCommands {

    @Autowired
    private NodeScopeContext nodeScopeContext;

    @Autowired
    private NodeMultiChannelService nodeMultiChannelService;

    @Command(name = "multichannel learn", alias = "ml", description = "Learns about multichannel specifics",
        availabilityProvider = "dongleRepositoryNodeAvailabilityhe")
    public String learnMultiChannelAvailabilities() throws SerialException {
        int nodeId = nodeScopeContext.getCurrentNodeId();
        NodeMultiChannelInformation information = nodeMultiChannelService.fetchMultiChannelAvailabilities(nodeId);
        StringBuilder builder = new StringBuilder(String.format(
            "\nNode id: %s (0x%02x)\n" +
            "End points count: %s\n" +
            "Aggregated end points count: %s\n" +
            "Is count dynamic: %s\n" +
            "Are capabilities identical: %s\n" +
            "\n",
            nodeId,
            nodeId,
            information.getEndPointsCount(),
            information.getAggregatedEndPointsCount(),
            information.isEndPointsCountDynamic(),
            information.isEndPointsCapabilitiesIdentical()
        ));

        for (int endPointIdx = 0; endPointIdx < information.getEndPointsCount(); endPointIdx++) {
            builder.append(String.format(
                "End point %s:\n" +
                "\tGeneric device class: %s\n" +
                "\tSpecific device class: %s\n" +
                "\tCommand classes: %s\n" +
                "\n",
                endPointIdx,
                information.getEndPointGenericClass(endPointIdx),
                information.getEndPointSpecificClass(endPointIdx),
                Arrays.toString(information.getEndPointCommandClasses(endPointIdx))
            ));
        }

        return builder.toString();
    }
}
