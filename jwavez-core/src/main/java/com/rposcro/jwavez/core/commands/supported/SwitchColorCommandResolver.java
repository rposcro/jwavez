package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.CommandTypesRegistry;
import com.rposcro.jwavez.core.commands.enums.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorSupportedReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SWITCH_COLOR)
public class SwitchColorCommandResolver extends AbstractCommandResolver<SwitchColorCommandType> {

  private static Map<SwitchColorCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, SwitchColorSupportedReport::new);
    suppliersPerCommandType.put(SwitchColorCommandType.SWITCH_COLOR_REPORT, SwitchColorReport::new);
  }

  public SwitchColorCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId) {
    SwitchColorCommandType commandType = CommandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException(CommandClass.CMD_CLASS_SWITCH_COLOR, commandType));
    return producer.apply(payloadBuffer, sourceNodeId);
  }
}
