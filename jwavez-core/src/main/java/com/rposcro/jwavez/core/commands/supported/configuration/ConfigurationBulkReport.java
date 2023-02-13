package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConfigurationBulkReport extends ZWaveSupportedCommand<ConfigurationCommandType> {

    private final static int MASK_HANDSHAKE = 0b0100_0000;
    private final static int MASK_VALUE_SIZE = 0b0000_0111;

    private int parametersOffset;
    private short parametersCount;
    private short reportsToFollow;
    private boolean handshake;
    private byte parameterSize;
    private long[] parameterValues;

    public ConfigurationBulkReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(ConfigurationCommandType.CONFIGURATION_BULK_REPORT, sourceNodeId);
        payload.rewind().skip(2);
        parametersOffset = payload.nextUnsignedWord();
        parametersCount = payload.nextUnsignedByte();
        reportsToFollow = payload.nextUnsignedByte();

        byte flags = payload.nextByte();
        handshake = (flags & MASK_HANDSHAKE) != 0;
        parameterSize = (byte) (flags & MASK_VALUE_SIZE);
        parameterValues = new long[parametersCount];

        for (int i = 0; i < parametersCount; i++) {
            parameterValues[i] = readValue(payload, parameterSize);
        }

        commandVersion = 2;
    }

    private long readValue(ImmutableBuffer payload, int valueSize) {
        switch (valueSize) {
            case 1:
                return payload.nextUnsignedByte();
            case 2:
                return payload.nextUnsignedWord();
            case 4:
                return payload.nextUnsignedDoubleWord();
            default:
                throw new IllegalArgumentException("Unsupported value size found in payload: " + valueSize);
        }
    }
}
