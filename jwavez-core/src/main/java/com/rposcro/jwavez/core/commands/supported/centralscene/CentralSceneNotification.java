package com.rposcro.jwavez.core.commands.supported.centralscene;

import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CentralSceneNotification extends ZWaveSupportedCommand<CentralSceneCommandType> {

    private short sequenceNumber;
    private short keyAttributes;
    private short sceneNumber;
    private boolean slowRefresh;

    public CentralSceneNotification(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, sourceNodeId);
        payload.skip(2);
        sequenceNumber = payload.nextUnsignedByte();
        final short attrField = payload.nextUnsignedByte();
        keyAttributes = (short) (attrField & 0x07);
        sceneNumber = payload.nextUnsignedByte();
        slowRefresh = (attrField & 0x80) != 0;
    }

    public CentralSceneKeyAttribute interpretKeyAttributes() {
        return CentralSceneKeyAttribute.ofCodeOptional((byte) keyAttributes).orElse(null);
    }

    @Override
    public String asNiceString() {
        return String.format("%s sequenceNumber(%02x) keyAttributes(%02x) sceneNumber(%02x) slowRefresh(%b)",
                super.asNiceString(), sequenceNumber, keyAttributes, sceneNumber, slowRefresh
        );
    }
}
