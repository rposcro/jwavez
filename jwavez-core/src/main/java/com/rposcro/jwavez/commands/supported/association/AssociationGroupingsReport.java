package com.rposcro.jwavez.commands.supported.association;

import com.rposcro.jwavez.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AssociationGroupingsReport extends ZWaveSupportedCommand<AssociationCommandType> {

  private short groupsCount;

  public AssociationGroupingsReport(ImmutableBuffer payload) {
    super(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT);
    groupsCount = payload.getUnsignedByte(2);
  }
}
