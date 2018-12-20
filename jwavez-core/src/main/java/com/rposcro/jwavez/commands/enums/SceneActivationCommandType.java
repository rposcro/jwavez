package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;

@CommandTypeEnum(commandClass = CommandClass.CMD_CLASS_SCENE_ACTIVATION)
public enum SceneActivationCommandType implements CommandType {

  SCENE_ACTIVATION_SET(0x01),
  ;

  private SceneActivationCommandType(int code) {
    CommandTypesRegistry.registerConstant(this, code);
  }
}
