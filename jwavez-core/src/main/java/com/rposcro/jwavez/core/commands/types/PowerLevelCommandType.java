package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_POWERLEVEL)
public enum PowerLevelCommandType implements CommandType {

    POWER_LEVEL_SET(1),
    POWER_LEVEL_GET(2),
    POWER_LEVEL_REPORT(3),
    POWER_LEVEL_TEST_NODE_SET(4),
    POWER_LEVEL_TEST_NODE_GET(5),
    POWER_LEVEL_TEST_NODE_REPORT(6);

    PowerLevelCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
