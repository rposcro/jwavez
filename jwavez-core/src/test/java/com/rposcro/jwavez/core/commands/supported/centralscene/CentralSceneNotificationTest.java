package com.rposcro.jwavez.core.commands.supported.centralscene;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CentralSceneNotificationTest {

    private final static byte SOURCE_NODE_ID = 0x0f;

    @Test
    public void testNotification() {
        byte[] payload = BuffersUtil.asByteArray("5b03 088203");
        CentralSceneNotification notification =
            new CentralSceneNotification(ImmutableBuffer.overBuffer(payload), new NodeId(SOURCE_NODE_ID));

        assertEquals(SOURCE_NODE_ID, notification.getSourceNodeId().getId());
        assertEquals(8, notification.getSequenceNumber());
        assertEquals(3, notification.getSceneNumber());
        assertEquals(2, notification.getKeyAttributes());
        assertEquals(CentralSceneKeyAttribute.KEY_HELD_DOWN, notification.interpretKeyAttributes());
        assertTrue(notification.isSlowRefresh());
    }
}
