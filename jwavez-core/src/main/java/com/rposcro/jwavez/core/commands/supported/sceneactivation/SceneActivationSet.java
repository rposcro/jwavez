package com.rposcro.jwavez.core.commands.supported.sceneactivation;

import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SceneActivationSet extends ZWaveSupportedCommand<SceneActivationCommandType> {

    private short sceneId;
    private short dimmingDuration;

    public SceneActivationSet(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(SceneActivationCommandType.SCENE_ACTIVATION_SET, sourceNodeId);
        sceneId = payload.getUnsignedByte(2);
        dimmingDuration = payload.getUnsignedByte(3);
        commandVersion = 1;
    }

    @Override
    public String asNiceString() {
        return String.format("%s sceneId(%02x) dimminingDuration(%02x)",
                super.asNiceString(), sceneId, dimmingDuration
        );
    }
}
