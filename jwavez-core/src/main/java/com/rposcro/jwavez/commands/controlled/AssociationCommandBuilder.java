package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.enums.CommandClass;

public class AssociationCommandBuilder {

  public ZWaveControlledCommand buildGetCommand(int groupNumber) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_GET.getCode(),
        (byte) groupNumber
    );
  }

  public ZWaveControlledCommand buildSetCommand(int groupNumber, int... nodeIds) {
    byte[] buffer = new byte[3 + nodeIds.length];
    buffer[0] = CommandClass.CMD_CLASS_ASSOCIATION.getCode();
    buffer[1] = AssociationCommandType.ASSOCIATION_SET.getCode();
    buffer[2] = (byte) groupNumber;
    for (int i = 0; i < nodeIds.length; i++) {
      buffer[3 + i] = (byte) nodeIds[i];
    }
    return new ZWaveControlledCommand(buffer);
  }

  public static ZWaveControlledCommand buildGetSupportedGroupingsCommand() {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_GROUPINGS_GET.getCode()
    );
  }

  public static ZWaveControlledCommand buildGetSpecificGroupCommand(int groupNumber) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_SPECIFIC_GROUP_GET.getCode(),
        (byte) groupNumber
    );
  }
}
