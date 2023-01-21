package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelAssociationGroupingsReport extends ZWaveSupportedCommand<MultiChannelAssociationCommandType> {

    private short groupsCount;

    public MultiChannelAssociationGroupingsReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_GROUPINGS_REPORT, sourceNodeId);
        groupsCount = payload.getUnsignedByte(2);
    }

    @Override
    public String asNiceString() {
        return String.format("%s groupsCnt(%02x)", super.asNiceString(), getGroupsCount());
    }
}
