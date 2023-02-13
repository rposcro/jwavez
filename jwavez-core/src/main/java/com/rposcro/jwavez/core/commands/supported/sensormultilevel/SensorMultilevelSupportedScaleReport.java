package com.rposcro.jwavez.core.commands.supported.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SensorMultilevelSupportedScaleReport extends ZWaveSupportedCommand<SensorMultilevelCommandType> {

    private byte sensorType;
    private byte scaleBitMask;

    public SensorMultilevelSupportedScaleReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SensorMultilevelCommandType.SENSOR_MULTILEVEL_SUPPORTED_SENSOR_REPORT, sourceNodeId);
        this.sensorType = payload.getByte(2);
        this.scaleBitMask = (byte) (payload.getByte(3) & 0x0f);
    }

    @Override
    public String asNiceString() {
        return String.format("%s sensorType(%02x) scaleBitMask(%02x)", super.asNiceString(), sensorType, scaleBitMask);
    }
}
