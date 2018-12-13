package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;

@CommandConstant(commandClass = CommandClass.CMD_CLASS_SCENE_ACTIVATION)
public enum SceneActivationCommandType implements CommandTypeEnum {

  SCENE_ACTIVATION_SET(0x01),
  ;

  private SceneActivationCommandType(int code) {
    CommandConstantsRegistry.registerConstant(this, code);
  }
}
