package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.multichannelassociation.MultiChannelAssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.multichannelassociation.MultiChannelAssociationReport;
import com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_GROUPINGS_REPORT;
import static com.rposcro.jwavez.core.commands.types.MultiChannelAssociationCommandType.MULTI_CHANNEL_ASSOCIATION_REPORT;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION)
public class MultiChannelAssociationCommandResolver extends AbstractCommandResolver<MultiChannelAssociationCommandType> {

  private static Map<MultiChannelAssociationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(MULTI_CHANNEL_ASSOCIATION_REPORT, MultiChannelAssociationReport::new);
    suppliersPerCommandType.put(MULTI_CHANNEL_ASSOCIATION_GROUPINGS_REPORT, MultiChannelAssociationGroupingsReport::new);
  }

  public MultiChannelAssociationCommandResolver() {
    super(suppliersPerCommandType);
  }
}
