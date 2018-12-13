package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.commands.enums.CommandTypeResolver;
import com.rposcro.jwavez.commands.enums.SceneActivationCommandType;
import com.rposcro.jwavez.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SCENE_ACTIVATION)
public class SceneActivationCommandResolver implements ZWaveCommandResolver {

  private static Map<SceneActivationCommandType, Function<ImmutableBuffer, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(SceneActivationCommandType.SCENE_ACTIVATION_SET, SceneActivationSet::new);
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer) {
    SceneActivationCommandType commandType = CommandTypeResolver.constantOfCode(SceneActivationCommandType.class, payloadBuffer.getByte(1));
    Function<ImmutableBuffer, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new IllegalArgumentException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer);
  }
}
