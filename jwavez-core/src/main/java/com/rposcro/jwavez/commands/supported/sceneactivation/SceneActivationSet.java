package com.rposcro.jwavez.commands.supported.sceneactivation;

import com.rposcro.jwavez.commands.enums.SceneActivationCommandType;
import com.rposcro.jwavez.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SceneActivationSet extends ZWaveSupportedCommand<SceneActivationCommandType> {

  private short sceneId;
  private short dimminingDuration;

  public SceneActivationSet(ImmutableBuffer payload) {
    super(SceneActivationCommandType.SCENE_ACTIVATION_SET);
    sceneId = payload.getUnsignedByte(2);
    dimminingDuration = payload.getUnsignedByte(3);
  }
}
