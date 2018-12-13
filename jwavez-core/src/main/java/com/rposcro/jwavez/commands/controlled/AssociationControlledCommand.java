package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.enums.CommandClass;

public class AssociationControlledCommand extends ControlledZWaveCommand {

  private AssociationControlledCommand(byte... commandPayload) {
    super(commandPayload);
  }

  public static AssociationControlledCommand buildGetCommand(int groupNumber) {
    return new AssociationControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_GET.getCode(),
        (byte) groupNumber
    );
  }

  public static AssociationControlledCommand buildSetCommand(int groupNumber, int... nodeIds) {
    byte[] buffer = new byte[3 + nodeIds.length];
    buffer[0] = CommandClass.CMD_CLASS_ASSOCIATION.getCode();
    buffer[1] = AssociationCommandType.ASSOCIATION_SET.getCode();
    buffer[2] = (byte) groupNumber;
    for (int i = 0; i < nodeIds.length; i++) {
      buffer[3 + i] = (byte) nodeIds[i];
    }
    return new AssociationControlledCommand(buffer);
  }

  public static AssociationControlledCommand buildGetSupportedGroupingsCommand() {
    return new AssociationControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_GROUPINGS_GET.getCode()
    );
  }

  public static AssociationControlledCommand buildGetSpecificGroupCommand(int groupNumber) {
    return new AssociationControlledCommand(
        CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
        AssociationCommandType.ASSOCIATION_SPECIFIC_GROUP_GET.getCode(),
        (byte) groupNumber
    );
  }
}
