package com.rposcro.jwavez.core.commands.supported.switchmultilevel;

import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SwitchMultilevelReport extends ZWaveSupportedCommand<SwitchMultiLevelCommandType> {

    private short currentValue;
    private short targetValue;
    private short duration;

    public SwitchMultilevelReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT, sourceNodeId);
        currentValue = payload.skip(2).nextUnsignedByte();
        if (payload.hasNext()) {
            targetValue = payload.nextUnsignedByte();
            duration = payload.nextUnsignedByte();
        }
    }
}
