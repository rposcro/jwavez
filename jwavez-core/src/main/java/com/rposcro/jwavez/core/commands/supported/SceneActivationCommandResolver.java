package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.commands.enums.SceneActivationCommandType;
import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SCENE_ACTIVATION)
public class SceneActivationCommandResolver extends AbstractCommandResolver<SceneActivationCommandType> {

  private static Map<SceneActivationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(SceneActivationCommandType.SCENE_ACTIVATION_SET, SceneActivationSet::new);
  }

  public SceneActivationCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId) {
    SceneActivationCommandType commandType = commandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer, sourceNodeId);
  }
}
