package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiChannelEncapsulationTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testEncapsulation1() {
        byte[] payload = new byte[]{
                0x60, 0x0d, 0x07, 0x01, 0x31, 0x05, 0x01, 0x22, 0x01, 0x51
        };

        MultiChannelCommandEncapsulation encapsulation = new MultiChannelCommandEncapsulation(
                ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, encapsulation.getSourceNodeId().getId());
        assertEquals(CommandClass.CMD_CLASS_MULTI_CHANNEL, encapsulation.getCommandClass());
        assertEquals(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, encapsulation.getCommandType());
        assertEquals(0x07, encapsulation.getSourceEndpointId());
        assertEquals(0x01, encapsulation.getDestinationEndpointId());
        assertArrayEquals(new byte[]{0x31, 0x05, 0x01, 0x22, 0x01, 0x51},
                encapsulation.getEncapsulatedCommandPayload());
    }
}
