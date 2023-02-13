package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SwitchColorReport extends ZWaveSupportedCommand<SwitchColorCommandType> {

    private short colorComponentId;
    private short currentValue;
    private short targetValue;
    private short duration;

    public SwitchColorReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchColorCommandType.SWITCH_COLOR_REPORT, sourceNodeId);
        payload.skip(2);
        colorComponentId = payload.nextUnsignedByte();
        currentValue = payload.nextUnsignedByte();

        if (payload.hasNext()) {
            targetValue = payload.nextUnsignedByte();
            duration = payload.nextUnsignedByte();
            commandVersion = 3;
        } else {
            commandVersion = 1;
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s colorComponentId(%02x) currentValue(%02x) targetValue(%02x) duration(%02x)",
                super.asNiceString(), colorComponentId, currentValue, targetValue, duration
        );
    }
}
