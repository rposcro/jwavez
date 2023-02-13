package com.rposcro.jwavez.core.commands.supported.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SensorMultilevelSupportedSensorReport extends ZWaveSupportedCommand<SensorMultilevelCommandType> {

    private byte[] bitMask;

    public SensorMultilevelSupportedSensorReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SensorMultilevelCommandType.SENSOR_MULTILEVEL_SUPPORTED_SENSOR_REPORT, sourceNodeId);
        payload.rewind().skip(2);
        bitMask = payload.cloneRemainingBytes();
        commandVersion = 5;
    }

    @Override
    public String asNiceString() {
        return String.format("%s bitMask[%s]", super.asNiceString(), BytesUtil.asString(bitMask));
    }
}
