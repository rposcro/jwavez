package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationBulkReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReportWith8Bits() {
        byte[] payload = BuffersUtil.asByteArray("7009 0010 03 00 01 77 88 99");

        ConfigurationBulkReport report = new ConfigurationBulkReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_CONFIGURATION, report.getCommandClass());
        assertEquals(ConfigurationCommandType.CONFIGURATION_BULK_REPORT, report.getCommandType());
        assertEquals(0x02, report.getCommandVersion());

        assertEquals(0x0010, report.getParametersOffset());
        assertEquals(0x03, report.getParametersCount());
        assertEquals(0x00, report.getReportsToFollow());
        assertEquals(0x01, report.getParameterSize());
        assertEquals(0x03, report.getParameterValues().length);
        assertEquals(0x77, report.getParameterValues()[0]);
        assertEquals(0x88, report.getParameterValues()[1]);
        assertEquals(0x99, report.getParameterValues()[2]);
    }

    @Test
    public void testReportWith16Bits() {
        byte[] payload = BuffersUtil.asByteArray("7009 0015 02 00 02 77 88 99 aa");

        ConfigurationBulkReport report = new ConfigurationBulkReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_CONFIGURATION, report.getCommandClass());
        assertEquals(ConfigurationCommandType.CONFIGURATION_BULK_REPORT, report.getCommandType());
        assertEquals(0x02, report.getCommandVersion());

        assertEquals(0x0015, report.getParametersOffset());
        assertEquals(0x02, report.getParametersCount());
        assertEquals(0x00, report.getReportsToFollow());
        assertEquals(0x02, report.getParameterSize());
        assertEquals(0x02, report.getParameterValues().length);
        assertEquals(0x7788, report.getParameterValues()[0]);
        assertEquals(0x99aa, report.getParameterValues()[1]);
    }
}
