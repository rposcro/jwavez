package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SWITCH_MULTILEVEL)
public enum SwitchMultiLevelCommandType implements CommandType {

    SWITCH_MULTILEVEL_SET(0x01),
    SWITCH_MULTILEVEL_GET(0x02),
    SWITCH_MULTILEVEL_REPORT(0x03),
    SWITCH_MULTILEVEL_START_LEVEL_CHANGE(0x04),
    SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE(0x05),
    ;

    SwitchMultiLevelCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
