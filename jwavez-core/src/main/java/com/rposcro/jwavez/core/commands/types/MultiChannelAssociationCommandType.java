package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_MULTI_CHANNEL_ASSOCIATION)
public enum MultiChannelAssociationCommandType implements CommandType {

    MULTI_CHANNEL_ASSOCIATION_SET(0x01),
    MULTI_CHANNEL_ASSOCIATION_GET(0x02),
    MULTI_CHANNEL_ASSOCIATION_REPORT(0x03),
    MULTI_CHANNEL_ASSOCIATION_REMOVE(0x04),
    MULTI_CHANNEL_ASSOCIATION_GROUPINGS_GET(0x05),
    MULTI_CHANNEL_ASSOCIATION_GROUPINGS_REPORT(0x06);

    MultiChannelAssociationCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
