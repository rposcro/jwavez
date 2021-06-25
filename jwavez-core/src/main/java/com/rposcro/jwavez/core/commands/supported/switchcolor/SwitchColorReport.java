package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.enums.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SwitchColorReport extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private short colorComponentId;
    private short colorValue;

    public SwitchColorReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_REPORT, sourceNodeId);
        colorComponentId = payload.nextUnsignedByte();
        colorValue = payload.nextUnsignedByte();
    }
}
