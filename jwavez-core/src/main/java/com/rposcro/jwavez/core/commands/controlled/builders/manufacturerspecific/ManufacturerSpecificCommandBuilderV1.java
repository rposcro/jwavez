package com.rposcro.jwavez.core.commands.controlled.builders.manufacturerspecific;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.ManufacturerSpecificCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class ManufacturerSpecificCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand() {
        return new ZWaveControlledCommand(CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC.getCode(), ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_GET.getCode());
    }
}
