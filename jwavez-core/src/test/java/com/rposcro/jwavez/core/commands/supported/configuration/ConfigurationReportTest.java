package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.model.BitLength;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReportWith8Bits() {
        byte[] payload = BytesUtil.asByteArray("7006c001ee");

        ConfigurationReport report = new ConfigurationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_CONFIGURATION, report.getCommandClass());
        assertEquals(ConfigurationCommandType.CONFIGURATION_REPORT, report.getCommandType());
        assertEquals(0x01, report.getCommandVersion());

        assertEquals(0xc0, report.getParameterNumber());
        assertEquals(0xee, report.getParameterValue());
        assertEquals(BitLength.BIT_LENGTH_8, report.getBitLength());
    }

    @Test
    public void testReportWith32Bits() {
        byte[] payload = BytesUtil.asByteArray("7006c004ee451290");

        ConfigurationReport report = new ConfigurationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_CONFIGURATION, report.getCommandClass());
        assertEquals(ConfigurationCommandType.CONFIGURATION_REPORT, report.getCommandType());
        assertEquals(0x01, report.getCommandVersion());

        assertEquals(0xc0, report.getParameterNumber());
        assertEquals(0xee451290l, report.getParameterValue());
        assertEquals(BitLength.BIT_LENGTH_32, report.getBitLength());
    }
}
