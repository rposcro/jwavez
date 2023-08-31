package com.rposcro.jwavez.core.commands.supported.manufacturerspecific;

import com.rposcro.jwavez.core.commands.types.ManufacturerSpecificCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ManufacturerSpecificReport extends ZWaveSupportedCommand<ManufacturerSpecificCommandType> {

    private final int manufacturerId;
    private final int productTypeId;
    private final int productId;

    public ManufacturerSpecificReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_REPORT, sourceNodeId);
        payload.skip(2);
        manufacturerId = payload.nextUnsignedWord();
        productTypeId = payload.nextUnsignedWord();
        productId = payload.nextUnsignedWord();
    }
}
