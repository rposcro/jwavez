package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SwitchColorSupportedReport extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private short colorComponentMask1;
    private short colorComponentMask2;

    public SwitchColorSupportedReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, sourceNodeId);
        colorComponentMask1 = payload.nextUnsignedByte();
        colorComponentMask2 = payload.nextUnsignedByte();
    }
}
