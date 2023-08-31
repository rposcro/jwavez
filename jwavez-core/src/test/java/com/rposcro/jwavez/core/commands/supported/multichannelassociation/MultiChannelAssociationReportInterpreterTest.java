package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.model.EndPointAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiChannelAssociationReportInterpreterTest {

    @Mock
    private MultiChannelAssociationReport report;

    @Test
    public void testNodeIdList() {
        when(report.getNodeIds()).thenReturn(new byte[] { (byte) 0xba, 0x54 });

        MultiChannelAssociationReportInterpreter interpreter = new MultiChannelAssociationReportInterpreter(report);

        assertEquals(2, interpreter.nodeIdList().size());
        assertEquals(0x0054, interpreter.nodeIdList().get(0));
        assertEquals(0x00ba, interpreter.nodeIdList().get(1));
    }

    @Test
    public void testNodeIdListEmpty() {
        when(report.getNodeIds()).thenReturn(new byte[0]);

        MultiChannelAssociationReportInterpreter interpreter = new MultiChannelAssociationReportInterpreter(report);

        assertTrue(interpreter.nodeIdList().isEmpty());
    }

    @Test
    public void testEndPointAddressList() {
        when(report.getEndPointIds())
                .thenReturn(new byte[][] { {(byte) 0xba, 0x54}, {(byte) 0xba, 0x12}, {0x05, 0x67} });

        MultiChannelAssociationReportInterpreter interpreter = new MultiChannelAssociationReportInterpreter(report);
        List<EndPointAddress> addresses = interpreter.endPointAddressList();

        assertEquals(3, addresses.size());
        assertEquals((byte) 0x05, addresses.get(0).getNodeId());
        assertEquals((byte) 0x67, addresses.get(0).getEndPointId());
        assertEquals((byte) 0xba, addresses.get(1).getNodeId());
        assertEquals((byte) 0x12, addresses.get(1).getEndPointId());
        assertEquals((byte) 0xba, addresses.get(2).getNodeId());
        assertEquals((byte) 0x54, addresses.get(2).getEndPointId());
    }

    @Test
    public void testEndPointAddressListFromBitMap() {
        when(report.getEndPointIds())
                .thenReturn(new byte[][] { {0x13, (byte) 0xc2} });

        MultiChannelAssociationReportInterpreter interpreter = new MultiChannelAssociationReportInterpreter(report);
        List<EndPointAddress> addresses = interpreter.endPointAddressList();

        assertEquals(2, addresses.size());
        assertEquals((byte) 0x13, addresses.get(0).getNodeId());
        assertEquals((byte) 0x02, addresses.get(0).getEndPointId());
        assertEquals((byte) 0x13, addresses.get(1).getNodeId());
        assertEquals((byte) 0x07, addresses.get(1).getEndPointId());
    }

    @Test
    public void testEndPointAddressListEmpty() {
        when(report.getEndPointIds()).thenReturn(new byte[0][2]);

        MultiChannelAssociationReportInterpreter interpreter = new MultiChannelAssociationReportInterpreter(report);
        List<EndPointAddress> addresses = interpreter.endPointAddressList();

        assertEquals(0, addresses.size());
    }
}
