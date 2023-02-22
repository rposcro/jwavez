package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import static com.rposcro.jwavez.core.utils.BitsUtil.extractValue;

@Getter
@ToString
public class MeterReport extends ZWaveSupportedCommand<MeterCommandType> {

    private final static int OFFSET_TO_DEFINITION_1 = 2;
    private final static int OFFSET_TO_DEFINITION_2 = 3;
    private final static int OFFSET_TO_MEASURE = 4;

    private byte meterType;
    private byte precision;
    private byte rateType;
    private byte scale;
    private byte measureSize;

    private int deltaTime;
    private short scale2;
    private long measure;
    private long previousMeasure;

    public MeterReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MeterCommandType.METER_REPORT, sourceNodeId);

        byte typeByte1 = payload.getByte(OFFSET_TO_DEFINITION_1);
        byte typeByte2 = payload.getByte(OFFSET_TO_DEFINITION_2);

        this.meterType = extractValue(typeByte1, 0, 0b11111);
        this.rateType = extractValue(typeByte1, 5, 0b11);
        this.measureSize = extractValue(typeByte2, 0, 0b111);
        this.scale = extractValue(typeByte2, 3, 0b11);
        this.precision = extractValue(typeByte2, 5, 0b111);
        this.measure = extractMeasure(payload, measureSize, OFFSET_TO_MEASURE);

        processVersionSpecific(payload);
    }

    public MeterReportInterpreter interpreter() {
        return new MeterReportInterpreter(this);
    }

    private void processVersionSpecific(ImmutableBuffer payload) {
        int tailSize = payload.length() - OFFSET_TO_MEASURE;

        if (tailSize == measureSize) {
            commandVersion = 1;
        } else {
            scale |= (payload.getByte(OFFSET_TO_DEFINITION_1) >> 5) & 0b100;
            deltaTime = payload.getUnsignedWord(OFFSET_TO_MEASURE + measureSize);

            if (deltaTime > 0) {
                previousMeasure = extractMeasure(payload, measureSize, OFFSET_TO_MEASURE + measureSize + 2);
            }

            int deltaFactor = deltaTime == 0 ? 1 : 2;
            boolean scale2Exists = (tailSize == (deltaFactor * measureSize) + 3);

            if (!scale2Exists) {
                commandVersion = (byte) ((scale & 0b100) == 0 ? 2 : 3);
            } else {
                scale2 = payload.getUnsignedByte(OFFSET_TO_MEASURE + (deltaFactor * measureSize) + 2);
                // scale2 should not exist when scale is not 0x7, so marking version as 0 for the case
                commandVersion = (byte) (scale == 0x7 ? 4 : 0);
            }
        }
    }

    private long extractMeasure(ImmutableBuffer payload, byte measureSize, int offset) {
        switch (measureSize) {
            case 1:
                return payload.getUnsignedByte(offset);
            case 2:
                return payload.getUnsignedWord(offset);
            case 4:
                return payload.getUnsignedDoubleWord(offset);
            default:
                return 0;
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s meterType(%02x) rateType(%02x) precision(%02x) measureSize(%02x)" +
                        " scale(%02x) scale2(%02x), measure(%08x) prevMeasure(%08x) deltaTime(%04x)",
                super.asNiceString(),
                meterType,
                rateType,
                precision,
                measureSize,
                scale,
                scale2,
                measure,
                previousMeasure,
                deltaTime
        );
    }
}
