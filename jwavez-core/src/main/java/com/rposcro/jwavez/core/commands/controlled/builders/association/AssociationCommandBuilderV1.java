package com.rposcro.jwavez.core.commands.controlled.builders.association;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;

import static com.rposcro.jwavez.core.utils.BytesUtil.toByteArray;

public class AssociationCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand(int groupNumber) {
        return buildGetCommand((byte) groupNumber);
    }

    public ZWaveControlledCommand buildGetCommand(byte groupNumber) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
                AssociationCommandType.ASSOCIATION_GET.getCode(),
                groupNumber
        );
    }

    public ZWaveControlledCommand buildSetCommand(int groupNumber, int... nodeIds) {
        return buildSetCommand((byte) groupNumber, toByteArray(nodeIds));
    }

    public ZWaveControlledCommand buildSetCommand(byte groupNumber, byte... nodeIds) {
        byte[] buffer = new byte[3 + nodeIds.length];
        buffer[0] = CommandClass.CMD_CLASS_ASSOCIATION.getCode();
        buffer[1] = AssociationCommandType.ASSOCIATION_SET.getCode();
        buffer[2] = groupNumber;
        for (int i = 0; i < nodeIds.length; i++) {
            buffer[3 + i] = nodeIds[i];
        }
        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildRemoveCommand(int groupNumber, int... nodeIds) {
      return buildRemoveCommand((byte) groupNumber, toByteArray(nodeIds));
    }

    public ZWaveControlledCommand buildRemoveCommand(byte groupNumber, byte... nodeIds) {
        byte[] buffer = new byte[3 + nodeIds.length];
        buffer[0] = CommandClass.CMD_CLASS_ASSOCIATION.getCode();
        buffer[1] = AssociationCommandType.ASSOCIATION_REMOVE.getCode();
        buffer[2] = (byte) groupNumber;
        for (int i = 0; i < nodeIds.length; i++) {
            buffer[3 + i] = (byte) nodeIds[i];
        }
        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildRemoveAllCommand(int groupNumber) {
        return buildRemoveAllCommand((byte) groupNumber);
    }

    public ZWaveControlledCommand buildRemoveAllCommand(byte groupNumber) {
        byte[] buffer = new byte[3];
        buffer[0] = CommandClass.CMD_CLASS_ASSOCIATION.getCode();
        buffer[1] = AssociationCommandType.ASSOCIATION_REMOVE.getCode();
        buffer[2] = groupNumber;
        return new ZWaveControlledCommand(buffer);
    }

    public ZWaveControlledCommand buildGetSupportedGroupingsCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
                AssociationCommandType.ASSOCIATION_GROUPINGS_GET.getCode()
        );
    }

    public ZWaveControlledCommand buildGetSpecificGroupCommand(int groupNumber) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
                AssociationCommandType.ASSOCIATION_SPECIFIC_GROUP_GET.getCode(),
                (byte) groupNumber
        );
    }
}
