package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_MULTI_CHANNEL)
public enum MultiChannelCommandType implements CommandType {

  // v1
  MULTI_INSTANCE_GET(0x04),
  MULTI_INSTANCE_REPORT(0x05),
  MULTI_INSTANCE_ENCAP(0x06),

  // v2, v3
  MULTI_CHANNEL_END_POINT_GET(0x07),
  MULTI_CHANNEL_END_POINT_REPORT(0x8),
  MULTI_CHANNEL_CAPABILITY_GET(0x09),
  MULTI_CHANNEL_CAPABILITY_REPORT(0x0A),
  MULTI_CHANNEL_END_POINT_FIND(0x0B),
  MULTI_CHANNEL_END_POINT_FIND_REPORT(0x0C),
  MULTI_CHANNEL_CMD_ENCAP(0x0D),

  // v4
  MULTI_CHANNEL_AGGREGATED_MEMBERS_GET(0x0E),
  MULTI_CHANNEL_AGGREGATED_MEMBERS_REPORT(0x0F),
  ;

  private MultiChannelCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
