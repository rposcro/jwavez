package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.model.NodeId;
import org.junit.jupiter.api.Test;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_ASSOCIATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_ASSOCIATION_GRP_INFO;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_NOTIFICATION;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SECURITY;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SENSOR_MULTILEVEL;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_UNKNOWN;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_ZWAVE_PLUS_INFO;
import static org.junit.Assert.assertEquals;

public class MultiChannelCapabilityReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void shouldParseReceivedCommand() {
        byte[] payload = new byte[]{
            0x60, 0x0a, 0x08, 0x21, 0x01, 0x5e, (byte) 0x85, (byte) 0x8e, 0x59, 0x31, 0x71, 0x6c, (byte) 0x98, (byte) 0x9f
        };

        MultiChannelCapabilityReport report = new MultiChannelCapabilityReport(
            ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(report.getSourceNodeId().getId(), SOURCE_NODE_ID);
        assertEquals(report.getEndPointId(), 8);
        assertEquals(report.getDecodedGenericDeviceClass(), GenericDeviceClass.GENERIC_TYPE_MULTILEVEL_SENSOR);
        assertEquals(report.getDecodedSpecificDeviceClass(), SpecificDeviceClass.SPECIFIC_TYPE_ROUTING_SENSOR_MULTILEVEL);
        assertEquals(report.getDecodedCommandClasses(),
            new CommandClass[] {
                CMD_CLASS_ZWAVE_PLUS_INFO,
                CMD_CLASS_ASSOCIATION,
                CMD_CLASS_MULTI_CHANNEL_ASSOCIATION,
                CMD_CLASS_ASSOCIATION_GRP_INFO,
                CMD_CLASS_SENSOR_MULTILEVEL,
                CMD_CLASS_NOTIFICATION,
                CMD_CLASS_UNKNOWN,
                CMD_CLASS_SECURITY,
                CMD_CLASS_UNKNOWN});
    }
}
