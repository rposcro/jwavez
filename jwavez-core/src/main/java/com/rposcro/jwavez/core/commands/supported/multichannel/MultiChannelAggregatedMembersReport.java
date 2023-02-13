package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelAggregatedMembersReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

    private byte aggregatedEndPoint;
    private byte[] endPointsMask;

    public MultiChannelAggregatedMembersReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelCommandType.MULTI_CHANNEL_AGGREGATED_MEMBERS_REPORT, sourceNodeId);
        payload.skip(2);
        aggregatedEndPoint = (byte) (payload.next() & 0x7f);

        int bitMaskCount = payload.nextUnsignedByte();
        endPointsMask = new byte[bitMaskCount];
        for (int i = 0; i < bitMaskCount; i++) {
            endPointsMask[i] = payload.next();
        }

        commandVersion = 4;
    }
}
