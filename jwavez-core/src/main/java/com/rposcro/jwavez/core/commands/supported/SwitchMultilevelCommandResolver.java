package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.enums.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SWITCH_MULTILEVEL)
public class SwitchMultilevelCommandResolver extends AbstractCommandResolver<SwitchMultiLevelCommandType> {

  private static Map<SwitchMultiLevelCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT, SwitchMultilevelReport::new);
  }

  public SwitchMultilevelCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId) {
    SwitchMultiLevelCommandType commandType = CommandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer, sourceNodeId);
  }
}
