package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_NOTIFICATION)
public enum NotificationCommandType implements CommandType {

    NOTIFICATION_GET(0x04),
    NOTIFICATION_REPORT(0x05),
    NOTIFICATION_SET(0x06),
    NOTIFICATION_SUPPORTED_GET(0x07),
    NOTIFICATION_SUPPORTED_REPORT(0x08),
    ;

    NotificationCommandType(int code) {
        CommandTypesRegistry.registerConstant(this, code);
    }
}
