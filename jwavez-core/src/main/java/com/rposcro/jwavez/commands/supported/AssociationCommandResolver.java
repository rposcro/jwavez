package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.commands.enums.CommandTypeResolver;
import com.rposcro.jwavez.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.utils.ImmutableBuffer;
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
    AssociationCommandType commandType = CommandTypeResolver.constantOfCode(AssociationCommandType.class, payloadBuffer.getByte(1));
    Function<ImmutableBuffer, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer);
  }
}
