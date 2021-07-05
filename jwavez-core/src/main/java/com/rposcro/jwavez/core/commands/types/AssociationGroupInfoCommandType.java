package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_ASSOCIATION_GRP_INFO)
public enum AssociationGroupInfoCommandType implements CommandType {

  ASSOCIATION_GROUP_NAME_GET(0x01),
  ASSOCIATION_GROUP_NAME_REPORT(0x02),
  ASSOCIATION_GROUP_INFO_GET(0x03),
  ASSOCIATION_GROUP_INFO_REPORT(0x04),
  ASSOCIATION_GROUP_COMMAND_LIST_GET(0x05),
  ASSOCIATION_GROUP_COMMAND_LIST_REPORT(0x06)
  ;

  private AssociationGroupInfoCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
