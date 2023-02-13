package com.rposcro.jwavez.core.commands.supported.switchcolor;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwitchColorSupportedReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testReportVersion1() {
        byte[] payload = BytesUtil.asByteArray("3304 1c01");

        SwitchColorSupportedReport report = new SwitchColorSupportedReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));
        List<ColorComponent> components = report.getColorComponents();

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_SWITCH_COLOR, report.getCommandClass());
        assertEquals(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, report.getCommandType());
        assertEquals(1, report.getCommandVersion());
        assertArrayEquals(new byte[] { 0x1c, 0x01 }, report.getColorComponentsMask());
        assertEquals(4, components.size());
        assertTrue(components.contains(ColorComponent.RED));
        assertTrue(components.contains(ColorComponent.GREEN));
        assertTrue(components.contains(ColorComponent.BLUE));
        assertTrue(components.contains(ColorComponent.INDEXED));
    }
}
