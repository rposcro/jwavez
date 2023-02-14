package com.rposcro.jwavez.core.commands.supported.sensormultilevel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorMultilevelReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReport1() {
        byte[] payload = new byte[]{
                0x31, 0x05, 0x01, 0x22, 0x00, (byte) 0xe5
        };

        SensorMultilevelReport sensorReport = new SensorMultilevelReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, sensorReport.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_SENSOR_MULTILEVEL, sensorReport.getCommandClass());
        assertEquals(SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT, sensorReport.getCommandType());
        assertEquals(1, sensorReport.getCommandVersion());

        assertEquals(0x01, sensorReport.getSensorType());
        assertEquals(0x01, sensorReport.getPrecision());
        assertEquals(0x00, sensorReport.getScaleValue());
        assertEquals(0x02, sensorReport.getMeasureSize());
        assertEquals(0x000000e5, sensorReport.getMeasureValue());
    }
}
