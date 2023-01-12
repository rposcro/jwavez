package com.rposcro.jwavez.core.commands.supported.sceneactivation;

import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SceneActivationSet extends ZWaveSupportedCommand<SceneActivationCommandType> {

  private short sceneId;
  private short dimminingDuration;

  public SceneActivationSet(ImmutableBuffer payload, NodeId sourceNodeId) {
    super(SceneActivationCommandType.SCENE_ACTIVATION_SET, sourceNodeId);
    sceneId = payload.getUnsignedByte(2);
    dimminingDuration = payload.getUnsignedByte(3);
  }

  @Override
  public String asNiceString() {
    return String.format("%s sceneId(%02x) dimminingDuration(%02x)",
            super.asNiceString(), sceneId, dimminingDuration
    );
  }
}
