package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_VERSION)
public enum VersionCommandType implements CommandType {

    VERSION_GET(0x11),
    VERSION_REPORT(0x12),
    VERSION_COMMAND_CLASS_GET(0x13),
    VERSION_COMMAND_CLASS_REPORT(0x14),
    VERSION_CAPABILITIES_GET(0x15),
    VERSION_CAPABILITIES_REPORT(0x16),
    VERSION_ZWAVE_SOFTWARE_GET(0x17),
    VERSION_ZWAVE_SOFTWARE_REPORT(0x18),
    ;

    VersionCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
