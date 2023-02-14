package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.model.MeterType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BytesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeterReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReportV1() {
        byte[] payload = BytesUtil.asByteArray("3202 0352 0566");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(1, meterReport.getCommandVersion());
        assertEquals(MeterType.WATER_METER.getCode(), meterReport.getMeterType());
        assertEquals(0, meterReport.getRateType());
        assertEquals(2, meterReport.getPrecision());
        assertEquals(2, meterReport.getScale());
        assertEquals(0, meterReport.getScale2());
        assertEquals(2, meterReport.getMeasureSize());
        assertEquals(0x00000566, meterReport.getMeasure());
        assertEquals(0x00, meterReport.getPreviousMeasure());
        assertEquals(0x00, meterReport.getDeltaTime());
    }

    @Test
    public void testReportV2WithDeltaSize4() {
        byte[] payload = BytesUtil.asByteArray("3202 2144 00aa0710 0120 0103ffac");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(2, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(1, meterReport.getRateType());
        assertEquals(2, meterReport.getPrecision());
        assertEquals(0, meterReport.getScale());
        assertEquals(0, meterReport.getScale2());
        assertEquals(4, meterReport.getMeasureSize());
        assertEquals(0x00aa0710, meterReport.getMeasure());
        assertEquals(0x103ffac, meterReport.getPreviousMeasure());
        assertEquals(0x120, meterReport.getDeltaTime());
    }

    @Test
    public void testReportV2WithDeltaSize2() {
        byte[] payload = BytesUtil.asByteArray("3202 2132 5011 3401 66fe");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(2, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(1, meterReport.getRateType());
        assertEquals(1, meterReport.getPrecision());
        assertEquals(2, meterReport.getScale());
        assertEquals(0, meterReport.getScale2());
        assertEquals(2, meterReport.getMeasureSize());
        assertEquals(0x00005011, meterReport.getMeasure());
        assertEquals(0x000066fe, meterReport.getPreviousMeasure());
        assertEquals(0x3401, meterReport.getDeltaTime());
    }

    @Test
    public void testReportV2NoDelta() {
        byte[] payload = BytesUtil.asByteArray("3202 2131 a7 0000");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(2, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(1, meterReport.getRateType());
        assertEquals(1, meterReport.getPrecision());
        assertEquals(2, meterReport.getScale());
        assertEquals(0, meterReport.getScale2());
        assertEquals(1, meterReport.getMeasureSize());
        assertEquals(0x000000a7, meterReport.getMeasure());
        assertEquals(0x00000000, meterReport.getPreviousMeasure());
        assertEquals(0x0000, meterReport.getDeltaTime());
    }

    @Test
    public void testReportV3WithDelta() {
        byte[] payload = BytesUtil.asByteArray("3202 a131 a7 0001 b0");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(3, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(1, meterReport.getRateType());
        assertEquals(1, meterReport.getPrecision());
        assertEquals(6, meterReport.getScale());
        assertEquals(0, meterReport.getScale2());
        assertEquals(1, meterReport.getMeasureSize());
        assertEquals(0x000000a7, meterReport.getMeasure());
        assertEquals(0x000000b0, meterReport.getPreviousMeasure());
        assertEquals(0x0001, meterReport.getDeltaTime());
    }

    @Test
    public void testReportV4NoDelta() {
        byte[] payload = BytesUtil.asByteArray("3202 a139 a7 0000 77");
        MeterReport meterReport = new MeterReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, meterReport.getSourceNodeId().getId());
        assertEquals(4, meterReport.getCommandVersion());
        assertEquals(MeterType.ELECTRIC_METER.getCode(), meterReport.getMeterType());
        assertEquals(1, meterReport.getRateType());
        assertEquals(1, meterReport.getPrecision());
        assertEquals(7, meterReport.getScale());
        assertEquals(0x77, meterReport.getScale2());
        assertEquals(1, meterReport.getMeasureSize());
        assertEquals(0x000000a7, meterReport.getMeasure());
        assertEquals(0x00000000, meterReport.getPreviousMeasure());
        assertEquals(0x0000, meterReport.getDeltaTime());
    }
}
