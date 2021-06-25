package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.CentralSceneCommandType;
import com.rposcro.jwavez.core.commands.enums.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneSupportedReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_CENTRAL_SCENE)
public class CentralSceneCommandResolver extends AbstractCommandResolver<CentralSceneCommandType> {

  private static Map<CentralSceneCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(CentralSceneCommandType.CENTRAL_SCENE_SUPPORTED_REPORT, CentralSceneSupportedReport::new);
    suppliersPerCommandType.put(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, CentralSceneNotification::new);
  }

  public CentralSceneCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId) {
    CentralSceneCommandType commandType = CommandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException(CommandClass.CMD_CLASS_CENTRAL_SCENE, commandType));
    return producer.apply(payloadBuffer, sourceNodeId);
  }
}
