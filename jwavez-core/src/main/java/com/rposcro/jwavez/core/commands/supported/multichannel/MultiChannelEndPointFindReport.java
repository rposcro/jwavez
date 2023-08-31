package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelEndPointFindReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

    private short reportsToFollow;
    private byte genericDeviceClass;
    private byte specificDeviceClass;
    private byte[] endPointsIds;

    public MultiChannelEndPointFindReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND_REPORT, sourceNodeId);
        payload.skip(2);
        reportsToFollow = payload.nextUnsignedByte();
        genericDeviceClass = payload.next();
        specificDeviceClass = payload.next();

        int endpointsCount = payload.available();
        endPointsIds = new byte[endpointsCount];
        for (int i = 0; i < endpointsCount; i++) {
            endPointsIds[i] = (byte) (payload.next() & 0x7f);
        }

        commandVersion = 3;
    }

    public GenericDeviceClass getDecodedGenericDeviceClass() {
        return GenericDeviceClass.ofCodeOptional(this.genericDeviceClass).orElse(null);
    }

    public SpecificDeviceClass getDecodedSpecificDeviceClass() {
        return SpecificDeviceClass.ofCodeOptional(this.specificDeviceClass, getDecodedGenericDeviceClass())
                .orElse(null);
    }
}
