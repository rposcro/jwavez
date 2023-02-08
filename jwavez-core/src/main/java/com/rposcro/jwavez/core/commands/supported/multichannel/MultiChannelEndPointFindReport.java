package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelEndPointFindReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

    private short reportsToFollow;
    private GenericDeviceClass genericDeviceClass;
    private SpecificDeviceClass specificDeviceClass;
    private byte[] endpointsIds;

    public MultiChannelEndPointFindReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND_REPORT, sourceNodeId);
        payload.skip(2);
        reportsToFollow = payload.nextUnsignedByte();
        genericDeviceClass = GenericDeviceClass.ofCode(payload.next());
        specificDeviceClass = SpecificDeviceClass.ofCode(payload.next(), genericDeviceClass);

        int endpointsCount = payload.available();
        endpointsIds = new byte[endpointsCount];
        for (int i = 0; i < endpointsCount; i++) {
            endpointsIds[i] = payload.next();
        }
    }
}
