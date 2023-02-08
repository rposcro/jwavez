package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_ASSOCIATION)
public enum AssociationCommandType implements CommandType {

    ASSOCIATION_SET(0x01),
    ASSOCIATION_GET(0x02),
    ASSOCIATION_REPORT(0x03),
    ASSOCIATION_REMOVE(0x04),
    ASSOCIATION_GROUPINGS_GET(0x05),
    ASSOCIATION_GROUPINGS_REPORT(0x06),
    ASSOCIATION_SPECIFIC_GROUP_GET(0x0B),
    ASSOCIATION_SPECIFIC_GROUP_REPORT(0x0C),
    ;

    private AssociationCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
