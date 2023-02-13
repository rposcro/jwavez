package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.BitLength;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConfigurationReport extends ZWaveSupportedCommand<ConfigurationCommandType> {

    private short parameterNumber;
    private byte parameterSize;
    private long parameterValue;

    public ConfigurationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(ConfigurationCommandType.CONFIGURATION_REPORT, sourceNodeId);
        parameterNumber = payload.getUnsignedByte(2);
        parameterSize = (byte) (payload.getByte(3) & 0x07);

        parameterValue = 0;
        for (int i = 0; i < parameterSize; i++) {
            parameterValue <<= 8;
            parameterValue |= (payload.getUnsignedByte(4 + i));
        }

        commandVersion = 1;
    }

    public BitLength getBitLength() {
        return BitLength.ofBytesNumber(parameterSize);
    }
}
