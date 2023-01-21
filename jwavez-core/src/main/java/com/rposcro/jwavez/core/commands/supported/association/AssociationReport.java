package com.rposcro.jwavez.core.commands.supported.association;

import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@ToString
public class AssociationReport extends ZWaveSupportedCommand<AssociationCommandType> {

    private short groupId;
    private short maxNodesCountSupported;
    private short nodesCount;
    private short reportsToFollow;
    private NodeId[] nodeIds;

    public AssociationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(AssociationCommandType.ASSOCIATION_REPORT, sourceNodeId);
        this.groupId = payload.getUnsignedByte(2);
        this.maxNodesCountSupported = payload.getUnsignedByte(3);
        this.reportsToFollow = payload.getUnsignedByte(4);
        this.nodesCount = (short) (payload.getLength() - 5);
        this.nodeIds = new NodeId[nodesCount];
        for (int i = 0; i < nodesCount; i++) {
            nodeIds[i] = new NodeId(payload.getByte(5 + i));
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s groupId(%02x) maxNodesCnt(%02x) nodesCnt(%02x) reportsToFollow(%02x) nodes[%s]",
                super.asNiceString(),
                getGroupId(),
                getMaxNodesCountSupported(),
                getNodesCount(),
                getReportsToFollow(),
                Stream.of(nodeIds).map(nodeId -> String.format("%02x", nodeId.getId())).collect(Collectors.joining(", "))
        );
    }
}
