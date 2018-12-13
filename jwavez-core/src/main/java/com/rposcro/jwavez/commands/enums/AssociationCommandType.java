package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;

@CommandConstant(commandClass = CommandClass.CMD_CLASS_ASSOCIATION)
public enum AssociationCommandType implements CommandTypeEnum {

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
    CommandConstantsRegistry.registerConstant(this, code);
  }
}
