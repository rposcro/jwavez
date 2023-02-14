package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BytesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiChannelAssociationReportTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testWithoutAssociationsWithoutMarker() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(0, report.getNodeIds().length);
        assertEquals(0, report.getEndPointIds().length);
    }

    @Test
    public void testWithoutAssociationsWithMarker() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00 00");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(0, report.getNodeIds().length);
        assertEquals(0, report.getEndPointIds().length);
    }

    @Test
    public void testWithNodesOnlyWithoutMarker() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00 5643");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(2, report.getNodeIds().length);
        assertEquals((byte) 0x56, report.getNodeIds()[0]);
        assertEquals((byte) 0x43, report.getNodeIds()[1]);
        assertEquals(0, report.getEndPointIds().length);
    }

    @Test
    public void testWithNodesOnlyWithMarker() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00 11d7c5 00");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(3, report.getNodeIds().length);
        assertEquals((byte) 0x11, report.getNodeIds()[0]);
        assertEquals((byte) 0xd7, report.getNodeIds()[1]);
        assertEquals((byte) 0xc5, report.getNodeIds()[2]);
        assertEquals(0, report.getEndPointIds().length);
    }

    @Test
    public void testWithEndpointsOnly() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00 00 571366af");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(0, report.getNodeIds().length);
        assertEquals(2, report.getEndPointIds().length);
        assertEquals((byte) 0x57, report.getEndPointIds()[0][0]);
        assertEquals((byte) 0x13, report.getEndPointIds()[0][1]);
        assertEquals((byte) 0x66, report.getEndPointIds()[1][0]);
        assertEquals((byte) 0xaf, report.getEndPointIds()[1][1]);
    }

    @Test
    public void testWithNodesAndEndpoints() {
        byte[] payload = BytesUtil.asByteArray("8e03 040a00 11d7 00 1344");
        MultiChannelAssociationReport report = new MultiChannelAssociationReport(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, report.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION, report.getCommandClass());
        assertEquals(MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT, report.getCommandType());
        assertEquals(2, report.getCommandVersion());

        assertEquals(0x04, report.getGroupId());
        assertEquals(0x0a, report.getMaxNodesCountSupported());
        assertEquals(0x00, report.getReportsToFollow());

        assertEquals(2, report.getNodeIds().length);
        assertEquals((byte) 0x11, report.getNodeIds()[0]);
        assertEquals((byte) 0xd7, report.getNodeIds()[1]);
        assertEquals(1, report.getEndPointIds().length);
        assertEquals((byte) 0x13, report.getEndPointIds()[0][0]);
        assertEquals((byte) 0x44, report.getEndPointIds()[0][1]);
    }
}
