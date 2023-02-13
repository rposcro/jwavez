package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.model.MeterType;
import com.rposcro.jwavez.core.model.MeterUnit;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeterReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReport1() {
        byte[] payload = new byte[]{
                0x32, 0x02, 0x21, 0x44, 0x00, (byte) 0xaa, 0x07, 0x10, 0x01, 0x20, 0x01, 0x03, (byte) 0xff, (byte) 0xac
        };

        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(2, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(MeterType.ELECTRIC_METER, meterReport.getDecodedMeterType());
        assertEquals(MeterUnit.kWh, meterReport.getDecodedMeterUnit());
        assertEquals(1, meterReport.getRateType());
        assertEquals(2, meterReport.getPrecision());
        assertEquals(0, meterReport.getScaleValue());
        assertEquals(0, meterReport.getScale2Value());
        assertEquals(4, meterReport.getMeasureSize());
        assertEquals(0x00aa0710, meterReport.getMeasure());
        assertEquals(0x103ffac, meterReport.getPreviousMeasure());
        assertEquals(0x120, meterReport.getDeltaTime());
    }

    @Test
    public void testReport2() {
        byte[] payload = new byte[]{
                0x32, 0x02, 0x21, 0x32, 0x50, 0x11, 0x34, 0x01, 0x66, (byte) 0xfe
        };

        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));
        System.out.println(meterReport.asNiceString());

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(2, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(MeterType.ELECTRIC_METER, meterReport.getDecodedMeterType());
        assertEquals(MeterUnit.W, meterReport.getDecodedMeterUnit());
        assertEquals(1, meterReport.getRateType());
        assertEquals(1, meterReport.getPrecision());
        assertEquals(2, meterReport.getScaleValue());
        assertEquals(0, meterReport.getScale2Value());
        assertEquals(2, meterReport.getMeasureSize());
        assertEquals(0x00005011, meterReport.getMeasure());
        assertEquals(0x000066fe, meterReport.getPreviousMeasure());
        assertEquals(0x3401, meterReport.getDeltaTime());
    }
}
