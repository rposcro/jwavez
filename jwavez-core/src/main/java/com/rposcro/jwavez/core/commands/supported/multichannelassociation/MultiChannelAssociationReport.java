package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.ZWaveConstants;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BytesUtil;
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
    private byte[] nodeIds;
    private byte[][] endPointIds;

    public MultiChannelAssociationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, sourceNodeId);
        payload.rewind().skip(2);

        this.groupId = payload.nextUnsignedByte();
        this.maxNodesCountSupported = payload.nextUnsignedByte();
        this.reportsToFollow = payload.nextUnsignedByte();

        this.nodeIds = extractNodes(payload);
        this.endPointIds = extractEndPoints(payload);
        this.commandVersion = 2;
    }

    public MultiChannelAssociationReportInterpreter interpreter() {
        return new MultiChannelAssociationReportInterpreter(this);
    }

    private byte[] extractNodes(ImmutableBuffer payload) {
        int nodesCount = 0;
        int bytesLeft = payload.available();

        while (bytesLeft > nodesCount && payload.getByte(NODES_INFO_OFFSET + nodesCount) != ZWaveConstants.MULTI_CHANNEL_ASSOCIATION_SET_MARKER) {
            nodesCount++;
        }

        byte[] nodeIds = new byte[nodesCount];

        for (int i = 0; i < nodesCount; i++) {
            nodeIds[i] = payload.nextByte();
        }

        return nodeIds;
    }

    private byte[][] extractEndPoints(ImmutableBuffer payload) {
        int endPointsCount = payload.available() > 0
                && payload.nextByte() == ZWaveConstants.MULTI_CHANNEL_ASSOCIATION_SET_MARKER ? payload.available() / 2 : 0;

        byte[][] endPointIds = new byte[endPointsCount][2];

        for (int i = 0; i < endPointsCount; i++) {
            endPointIds[i][0] = payload.nextByte();
            endPointIds[i][1] = payload.nextByte();
        }

        return endPointIds;
    }

    @Override
    public String asNiceString() {
        return String.format("%s groupId(%02x) maxNodesCnt(%02x) reportsToFollow(%02x) nodes[%s] endPoints[%s]",
                super.asNiceString(),
                getGroupId(),
                getMaxNodesCountSupported(),
                getReportsToFollow(),
                BytesUtil.asString(nodeIds),
                Stream.of(endPointIds).map(epId -> String.format("%02x-%02x", epId[0], epId[1])).collect(Collectors.joining(" "))
        );
    }
}
