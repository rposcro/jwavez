package com.rposcro.jwavez.core.commands.controlled.builders.multichannelassociation;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.ZWaveConstants;
import com.rposcro.jwavez.core.model.EndPointId;
import com.rposcro.jwavez.core.model.NodeId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION;
import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_GET;
import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_GROUPINGS_GET;
import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REMOVE;
import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_SET;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class MultiChannelAssociationCommandBuilderV2 {

    public ZWaveControlledCommand buildSetCommand(int groupNumber, NodeId... nodeIds) {
        return buildSetCommand(groupNumber, nodeIds, null);
    }

    public ZWaveControlledCommand buildSetCommand(int groupNumber, EndPointId... endPointIds) {
        return buildSetCommand(groupNumber, null, endPointIds);
    }

    public ZWaveControlledCommand buildSetCommand(int groupNumber, NodeId[] nodeIds, EndPointId[] endPointIds) {
        byte[] buffer = new byte[3 + computeIdsPayloadSize(nodeIds, endPointIds)];
        buffer[0] = CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode();
        buffer[1] = MULTI_CHANNEL_ASSOCIATION_SET.getCode();
        buffer[2] = (byte) groupNumber;

        int offset = fillNodeIds(buffer, 3, nodeIds);
        fillEndPointIds(buffer, offset, endPointIds);

        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildGetCommand(int groupNumber) {
        return buildGetCommand((byte) groupNumber);
    }

    public ZWaveControlledCommand buildGetCommand(byte groupNumber) {
        return new ZWaveControlledCommand(
                CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode(),
                MULTI_CHANNEL_ASSOCIATION_GET.getCode(),
                groupNumber
        );
    }

    public ZWaveControlledCommand buildRemoveAllAssociationsCommand() {
        byte[] buffer = new byte[2];
        buffer[0] = CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode();
        buffer[1] = MULTI_CHANNEL_ASSOCIATION_REMOVE.getCode();
        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildRemoveAllGroupAssociationsCommand(int groupNumber) {
        byte[] buffer = new byte[3];
        buffer[0] = CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode();
        buffer[1] = MULTI_CHANNEL_ASSOCIATION_REMOVE.getCode();
        buffer[2] = (byte) groupNumber;
        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildRemoveCommand(int groupNumber, NodeId... nodeIds) {
        return buildRemoveCommand(groupNumber, nodeIds, null);
    }

    public ZWaveControlledCommand buildRemoveCommand(int groupNumber, EndPointId... endPointIds) {
        return buildRemoveCommand(groupNumber, null, endPointIds);
    }

    public ZWaveControlledCommand buildRemoveCommand(int groupNumber, NodeId[] nodeIds, EndPointId[] endPointIds) {
        byte[] buffer = new byte[3 + computeIdsPayloadSize(nodeIds, endPointIds)];
        buffer[0] = CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode();
        buffer[1] = MULTI_CHANNEL_ASSOCIATION_REMOVE.getCode();
        buffer[2] = (byte) groupNumber;

        int offset = fillNodeIds(buffer, 3, nodeIds);
        fillEndPointIds(buffer, offset, endPointIds);

        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildGetSupportedGroupingsCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_MULTI_CHANNEL_ASSOCIATION.getCode(),
                MULTI_CHANNEL_ASSOCIATION_GROUPINGS_GET.getCode()
        );
    }

    private int fillNodeIds(byte[] buffer, int offset, NodeId[] nodeIds) {
        if (nodeIds != null && nodeIds.length > 0) {
            for (NodeId nodeId : nodeIds) {
                buffer[offset++] = nodeId.getId();
            }
        }
        return offset;
    }

    private int fillEndPointIds(byte[] buffer, int offset, EndPointId[] endPointIds) {
        if (endPointIds != null && endPointIds.length > 0) {
            buffer[offset++] = ZWaveConstants.MULTI_CHANNEL_ASSOCIATION_SET_MARKER;
            for (EndPointId endPointId : endPointIds) {
                buffer[offset++] = endPointId.getNodeId();
                buffer[offset++] = endPointId.getEndPointId();
            }
        }
        return offset;
    }

    private int computeIdsPayloadSize(NodeId[] nodeIds, EndPointId[] endPointIds) {
        int size = nodeIds != null ? nodeIds.length : 0;
        size += nodeIds != null ? nodeIds.length : 0;

        if (endPointIds != null && endPointIds.length > 0) {
            size++;
            size += 1 + (endPointIds.length * 2);
        }

        return size;
    }
}
