package com.rposcro.jwavez.core.commands.supported.centralscene;

import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CentralSceneNotification extends ZWaveSupportedCommand<CentralSceneCommandType> {

    private short sequenceNumber;
    private short keyAttributes;
    private short sceneNumber;

    public CentralSceneNotification(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, sourceNodeId);
        payload.skip(2);
        sequenceNumber = payload.nextUnsignedByte();
        keyAttributes = payload.nextUnsignedByte();
        sceneNumber = payload.nextUnsignedByte();
    }
}
