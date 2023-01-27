package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.constants.ZWaveConstants;
import com.rposcro.jwavez.core.model.EndPointId;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@ToString
public class MultiChannelAssociationReport extends ZWaveSupportedCommand<MultiChannelAssociationCommandType> {

    private final static int NODES_INFO_OFFSET = 5;

    private short groupId;
    private short maxNodesCountSupported;
    private short reportsToFollow;
    private NodeId[] nodeIds;
    private EndPointId[] endPointIds;

    public MultiChannelAssociationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, sourceNodeId);
        payload.rewind().skip(2);
        this.groupId = payload.nextUnsignedByte();
        this.maxNodesCountSupported = payload.nextUnsignedByte();
        this.reportsToFollow = payload.nextUnsignedByte();

        this.nodeIds = parseNodes(payload);
        this.endPointIds = parseEndPoints(payload);
    }

    private NodeId[] parseNodes(ImmutableBuffer payload) {
        int nodesCount = 0;
        int bytesLeft = payload.available();

        while (bytesLeft > nodesCount && payload.getByte(NODES_INFO_OFFSET + nodesCount) != ZWaveConstants.MULTI_CHANNEL_ASSOCIATION_SET_MARKER) {
            nodesCount++;
        }

        NodeId[] nodes = new NodeId[nodesCount];

        for (int i = 0; i < nodesCount; i++) {
            nodes[i] = new NodeId(payload.nextByte());
        }

        return nodes;
    }

    private EndPointId[] parseEndPoints(ImmutableBuffer payload) {
        int endPointsCount = payload.available() > 0
                && payload.nextByte() == ZWaveConstants.MULTI_CHANNEL_ASSOCIATION_SET_MARKER ? payload.available() / 2 : 0;
        EndPointId[] endPoints = new EndPointId[endPointsCount];

        for (int i = 0; i < endPointsCount; i++) {
            endPoints[i] = new EndPointId(payload.nextByte(), payload.nextByte());
        }

        return endPoints;
    }

    @Override
    public String asNiceString() {
        return String.format("%s groupId(%02x) maxNodesCnt(%02x) reportsToFollow(%02x) nodes[%s] endPoints[%s]",
                super.asNiceString(),
                getGroupId(),
                getMaxNodesCountSupported(),
                getReportsToFollow(),
                Stream.of(nodeIds).map(nodeId -> String.format("%02x", nodeId.getId())).collect(Collectors.joining(", ")),
                Stream.of(endPointIds).map(epId -> String.format("%02x-%02x", epId.getNodeId(), epId.getEndPointId())).collect(Collectors.joining(", "))
        );
    }
}
