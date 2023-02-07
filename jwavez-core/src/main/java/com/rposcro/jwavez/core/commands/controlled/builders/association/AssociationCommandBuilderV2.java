package com.rposcro.jwavez.core.commands.controlled.builders.association;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;

import static com.rposcro.jwavez.core.utils.BytesUtil.toByteArray;

public class AssociationCommandBuilderV2 extends AssociationCommandBuilderV1 {

    public ZWaveControlledCommand buildRemoveAllNodesCommand(int... nodeIds) {
        return buildRemoveAllNodesCommand(toByteArray(nodeIds));
    }

    public ZWaveControlledCommand buildRemoveAllNodesCommand(byte... nodeIds) {
        return buildRemoveCommand((byte) 0, nodeIds);
    }

    public ZWaveControlledCommand buildRemoveAllCommand() {
        return buildRemoveAllCommand((byte) 0);
    }

    @Deprecated
    public ZWaveControlledCommand buildGetSpecificGroupCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_ASSOCIATION.getCode(),
                AssociationCommandType.ASSOCIATION_SPECIFIC_GROUP_GET.getCode()
        );
    }
}
