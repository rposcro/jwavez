package com.rposcro.jwavez.core.commands.supported.basic;

import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BasicReport extends ZWaveSupportedCommand<BasicCommandType> {

    private short value;

    public BasicReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(BasicCommandType.BASIC_REPORT, sourceNodeId);
        value = payload.getUnsignedByte(2);
    }

    @Override
    public String asNiceString() {
        return String.format("%s value(%02x)",
                super.asNiceString(), value
        );
    }
}
