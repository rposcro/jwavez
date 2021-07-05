package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.commands.types.ManufacturerSpecificCommandType;
import com.rposcro.jwavez.core.classes.CommandClass;

public class ManufacturerSpecificCommandBuilder {

  public ZWaveControlledCommand buildGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC.getCode(), ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_GET.getCode());
  }
}
