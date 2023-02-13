package com.rposcro.jwavez.core.commands.supported.association;

import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AssociationGroupingsReport extends ZWaveSupportedCommand<AssociationCommandType> {

    private short groupsCount;

    public AssociationGroupingsReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT, sourceNodeId);
        groupsCount = payload.getUnsignedByte(2);
    }

    @Override
    public String asNiceString() {
        return String.format("%s groupsCnt(%02x)", super.asNiceString(), getGroupsCount());
    }
}
