package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchColorReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReportVersion1() {
        byte[] payload = BytesUtil.asByteArray("33040150");

        SwitchColorReport report = new SwitchColorReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_SWITCH_COLOR, report.getCommandClass());
        assertEquals(SwitchColorCommandType.SWITCH_COLOR_REPORT, report.getCommandType());
        assertEquals(0x01, report.getColorComponentId());
        assertEquals(0x50, report.getCurrentValue());
        assertEquals(0x01, report.getCommandVersion());
    }

    @Test
    public void testReportVersion3() {
        byte[] payload = BytesUtil.asByteArray("33040150a905");

        SwitchColorReport report = new SwitchColorReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_SWITCH_COLOR, report.getCommandClass());
        assertEquals(SwitchColorCommandType.SWITCH_COLOR_REPORT, report.getCommandType());
        assertEquals(0x01, report.getColorComponentId());
        assertEquals(0x50, report.getCurrentValue());
        assertEquals(0xa9, report.getTargetValue());
        assertEquals(0x05, report.getDuration());
        assertEquals(0x03, report.getCommandVersion());
    }
}
