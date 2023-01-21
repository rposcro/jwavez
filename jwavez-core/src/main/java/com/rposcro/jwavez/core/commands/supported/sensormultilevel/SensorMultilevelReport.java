package com.rposcro.jwavez.core.commands.supported.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BitsUtil;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SensorMultilevelReport extends ZWaveSupportedCommand<SensorMultilevelCommandType> {

    private byte sensorTypeCode;
    private byte precision;
    private byte scaleValue;
    private byte measureSize;
    private long measureValue;

    public SensorMultilevelReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT, sourceNodeId);
        payload.rewind().skip(2);
        this.sensorTypeCode = payload.nextByte();

        byte def = payload.nextByte();
        this.precision = BitsUtil.extractValue(def, 5, 7);
        this.scaleValue = BitsUtil.extractValue(def, 3, 3);
        this.measureSize = BitsUtil.extractValue(def, 0, 7);
        this.measureValue = parseMeasureValue(measureSize, payload);
    }

    private long parseMeasureValue(int size, ImmutableBuffer payload) {
        long value = 0;
        for (; size > 0; size--) {
            value <<= 8;
            value |= (payload.nextByte()) & 0xff;
        }
        return value;
    }

    @Override
    public String asNiceString() {
        return String.format("%s typeCode(%02x), precision(%02x) scaleValue(%02x) measureSize(%02x) measure(dec: %s)",
                super.asNiceString(),
                sensorTypeCode,
                precision,
                scaleValue,
                measureSize,
                measureValue
        );
    }
}
