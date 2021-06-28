package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_ASSOCIATION)
public class AssociationCommandResolver extends AbstractCommandResolver<AssociationCommandType> {

  private static Map<AssociationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(AssociationCommandType.ASSOCIATION_REPORT, AssociationReport::new);
    suppliersPerCommandType.put(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT, AssociationGroupingsReport::new);
  }

  public AssociationCommandResolver() {
    super(suppliersPerCommandType);
  }
}
