package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;
import com.rposcro.jwavez.core.constants.MeterType;
import com.rposcro.jwavez.core.constants.MeterUnit;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

import static com.rposcro.jwavez.core.utils.BitsUtil.extractValue;

@Getter
@ToString
public class MeterReport extends ZWaveSupportedCommand<MeterCommandType> {

    private final static int OFFSET_TO_MEASURE = 4;
    private final static int OFFSET_TO_DEFINITION_1 = 2;
    private final static int OFFSET_TO_DEFINITION_2 = 3;

    private byte meterTypeCode;
    private MeterType meterType;
    private MeterUnit meterUnit;
    private byte scaleValue;
    private byte scale2Value;
    private byte rateType;
    private byte precision;
    private byte measureSize;

    private long measure;
    private long previousMeasure;
    private int deltaTime;

    private byte commandVersion;

    public MeterReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(MeterCommandType.METER_REPORT, sourceNodeId);

        byte typeByte1 = payload.getByte(OFFSET_TO_DEFINITION_1);
        byte typeByte2 = payload.getByte(OFFSET_TO_DEFINITION_2);

        this.meterTypeCode = extractValue(typeByte1, 0, 0b11111);
        this.meterType = MeterType.ofCodeOptional(meterTypeCode).orElse(null);
        this.rateType = extractValue(typeByte1, 5, 0b11);
        this.measureSize = extractValue(typeByte2, 0, 0b111);
        this.commandVersion = recognizeCommandVersion(payload, measureSize);
        this.precision = extractValue(typeByte2, 5, 0b111);
        this.scaleValue = extractScale(payload, commandVersion, measureSize);
        this.scale2Value = extractScale2(payload, commandVersion, measureSize);
        this.meterUnit = recognizeMeterUnit(meterType, scaleValue);
        this.deltaTime = extractDeltaTime(payload, commandVersion, measureSize);
        this.measure = extractMeasure(payload, measureSize, OFFSET_TO_MEASURE);
        this.previousMeasure = extractPreviousMeasure(payload, commandVersion, measureSize);
    }

    private byte extractScale(ImmutableBuffer payload, byte commandVersion, byte measureSize) {
        byte scale = extractValue(payload.getByte(OFFSET_TO_DEFINITION_2), 3, 0b11);
        if (commandVersion > 1) {
            scale = (byte) (scale | ((payload.getByte(OFFSET_TO_DEFINITION_1) >> 5) & 0b100));
        }
        return scale;
    }

    private byte extractScale2(ImmutableBuffer payload, byte commandVersion, byte measureSize) {
        if (commandVersion >= 4) {
            return payload.getByte(OFFSET_TO_MEASURE + (measureSize * 2) + 2 + 1);
        }
        return 0;
    }

    private long extractPreviousMeasure(ImmutableBuffer payload, byte commandVersion, byte measureSize) {
        if (commandVersion > 1) {
            return extractMeasure(payload, measureSize, OFFSET_TO_MEASURE + measureSize + 2);
        }
        return 0;
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

    private int extractDeltaTime(ImmutableBuffer payload, byte commandVersion, byte measureSize) {
        if (commandVersion > 1) {
            return payload.getUnsignedWord(OFFSET_TO_MEASURE + measureSize);
        }
        return 0;
    }

    private byte recognizeCommandVersion(ImmutableBuffer payload, byte measureSize) {
        int diff = payload.getLength() - OFFSET_TO_MEASURE;
        if (diff == measureSize) {
            return 1;
        } else if (diff == (measureSize * 2) + 2) {
            return 2;
        } else if (diff == (measureSize * 2) + 3) {
            return 4;
        }
        return 0;
    }

    private MeterUnit recognizeMeterUnit(MeterType meterType, int scaleValue) {
        if (meterType == null) {
            return null;
        }

        switch (meterType) {
            case ELECTRIC_METER:
                return recognizeMeterUnitForElectricMeter(scaleValue);
            case GAS_METER:
                return recognizeMeterUnitForGasMeter(scaleValue);
            case WATER_METER:
                return recognizeMeterUnitForWaterMeter(scaleValue);
            case HEATING_METER:
                return recognizeMeterUnitForHeatingMeter(scaleValue);
            case COOLING_METER:
                return recognizeMeterUnitForCoolingMeter(scaleValue);
            default:
                return null;
        }
    }

    private MeterUnit recognizeMeterUnitForElectricMeter(int scaleValue) {
        switch (scaleValue) {
            case 0:
                return MeterUnit.kWh;
            case 1:
                return MeterUnit.kVAh;
            case 2:
                return MeterUnit.W;
            case 3:
                return MeterUnit.PulseCount;
            case 4:
                return MeterUnit.V;
            case 5:
                return MeterUnit.A;
            case 6:
                return MeterUnit.PowerFactor;
            default:
                return MeterUnit.Unspecified;
        }
    }

    private MeterUnit recognizeMeterUnitForGasMeter(int scaleValue) {
        switch (scaleValue) {
            case 0:
                return MeterUnit.CubicMeters;
            case 1:
                return MeterUnit.CubicFeet;
            case 3:
                return MeterUnit.PulseCount;
            default:
                return MeterUnit.Unspecified;
        }
    }

    private MeterUnit recognizeMeterUnitForWaterMeter(int scaleValue) {
        switch (scaleValue) {
            case 0:
                return MeterUnit.CubicMeters;
            case 1:
                return MeterUnit.CubicFeet;
            case 2:
                return MeterUnit.UsGallons;
            case 3:
                return MeterUnit.PulseCount;
            default:
                return MeterUnit.Unspecified;
        }
    }

    private MeterUnit recognizeMeterUnitForHeatingMeter(int scaleValue) {
        switch (scaleValue) {
            case 0:
                return MeterUnit.kWh;
            default:
                return MeterUnit.Unspecified;
        }
    }

    private MeterUnit recognizeMeterUnitForCoolingMeter(int scaleValue) {
        switch (scaleValue) {
            case 0:
                return MeterUnit.kWh;
            default:
                return MeterUnit.Unspecified;
        }
    }

    @Override
    public String asNiceString() {
        return String.format("%s version(%02x), %s(%02x) %s(%02x) rateType(%02x) precision(%02x) measureSize(%02x)" +
                        ", scale(%02x), scale2(%02x), measure(%08x), prevMeasure(%08x), deltaTime(%02x)",
                super.asNiceString(),
                commandVersion,
                meterType.name(),
                meterTypeCode,
                meterUnit,
                scaleValue,
                rateType,
                precision,
                measureSize,
                scaleValue,
                scale2Value,
                measure,
                previousMeasure,
                deltaTime
        );
    }
}
