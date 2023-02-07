package com.rposcro.jwavez.core.commands.controlled.builders.configuration;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;

public class ConfigurationCommandBuilderV3 extends ConfigurationCommandBuilderV2 {

    public ZWaveControlledCommand buildGetNameCommand(int parameterNumber) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
                ConfigurationCommandType.CONFIGURATION_NAME_GET.getCode(),
                (byte) (parameterNumber >> 8),
                (byte) (parameterNumber));
    }
}
