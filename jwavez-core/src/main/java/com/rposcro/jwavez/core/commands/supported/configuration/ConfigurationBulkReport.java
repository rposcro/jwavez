package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConfigurationBulkReport extends ZWaveSupportedCommand<ConfigurationCommandType> {

    private final static int OFFSET_OFFSET = 2;
    private final static int OFFSET_COUNT = 4;
    private final static int OFFSET_FOLLOW = 5;
    private final static int OFFSET_FLAGS = 6;
    private final static int OFFSET_VALUES = 7;
    private final static int MASK_HANDSHAKE = 0b0100_0000;
    private final static int MASK_VALUE_SIZE = 0b0000_0111;


    private int parametersOffset;
    private int parametersCount;
    private short reportsToFollow;
    private boolean handshake;
    private byte valueSize;
    private int[] values;

    public ConfigurationBulkReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(ConfigurationCommandType.CONFIGURATION_BULK_REPORT, sourceNodeId);
        parametersOffset = payload.getUnsignedWord(OFFSET_OFFSET);
        parametersCount = payload.getUnsignedByte(OFFSET_COUNT);
        reportsToFollow = payload.getUnsignedByte(OFFSET_FOLLOW);

        byte flags = payload.getByte(OFFSET_FLAGS);
        handshake = (flags & MASK_HANDSHAKE) != 0;
        valueSize = (byte) (flags & MASK_VALUE_SIZE);
        values = new int[parametersCount];

        for (int i = 0; i < parametersCount; i++) {
            values[i] = readValue(payload, valueSize, i * valueSize);
        }
    }

    private int readValue(ImmutableBuffer payload, int valueSize, int offset) {
        switch (valueSize) {
            case 1:
                return payload.getByte(offset);
            case 2:
                return payload.getWord(offset);
            case 4:
                return payload.getDoubleWord(offset);
            default:
                throw new IllegalArgumentException("Unsupported value size found in payload: " + valueSize);
        }
    }
}
