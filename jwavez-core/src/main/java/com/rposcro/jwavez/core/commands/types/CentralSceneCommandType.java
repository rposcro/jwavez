package com.rposcro.jwavez.core.commands.types;

import com.rposcro.jwavez.core.classes.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_CENTRAL_SCENE)
public enum CentralSceneCommandType implements CommandType {

  CENTRAL_SCENE_SUPPORTED_GET(0x01),
  CENTRAL_SCENE_SUPPORTED_REPORT(0x02),
  CENTRAL_SCENE_NOTIFICATION(0x03),
  CENTRAL_SCENE_CONFIGURATION_SET(0x04),
  CENTRAL_SCENE_CONFIGURATION_GET(0x05),
  CENTRAL_SCENE_CONFIGURATION_REPORT(0x06),
  ;

  private CentralSceneCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
