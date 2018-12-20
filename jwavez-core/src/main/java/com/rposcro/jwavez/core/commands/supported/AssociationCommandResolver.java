package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_ASSOCIATION)
public class AssociationCommandResolver extends AbstractCommandResolver<AssociationCommandType> {

  private static Map<AssociationCommandType, Function<ImmutableBuffer, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(AssociationCommandType.ASSOCIATION_GROUPINGS_GET, AssociationGroupingsReport::new);
  }

  public AssociationCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer) {
    AssociationCommandType commandType = commandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    Function<ImmutableBuffer, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer);
  }
}
