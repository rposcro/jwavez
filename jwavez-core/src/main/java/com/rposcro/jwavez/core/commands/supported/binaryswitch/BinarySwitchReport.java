package com.rposcro.jwavez.core.commands.supported.binaryswitch;

import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinarySwitchReport extends ZWaveSupportedCommand<SwitchBinaryCommandType> {

    private short value;
    private short targetValue;
    private short duration;

    public BinarySwitchReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, sourceNodeId);
        payload.skip(2);
        value = payload.nextUnsignedByte();
        if (payload.hasNext()) {
            targetValue = payload.nextUnsignedByte();
            duration = payload.nextUnsignedByte();
        }
        commandVersion = recognizeVersion(payload);
    }

    public byte recognizeVersion(ImmutableBuffer payload) {
        int length = payload.getLength();
        if (length == 3) {
            return 1;
        } else if (length == 5) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s value(%02x) targetValues(%02x) duration(%02x)",
                super.asNiceString(), value, targetValue, duration
        );
    }
}
