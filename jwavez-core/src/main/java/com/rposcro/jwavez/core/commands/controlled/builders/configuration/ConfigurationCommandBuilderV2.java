package com.rposcro.jwavez.core.commands.controlled.builders.configuration;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class ConfigurationCommandBuilderV2 extends ConfigurationCommandBuilderV1 {

    public ZWaveControlledCommand buildBulkGetParameterCommand(int parameterOffset, int parametersCount) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_CONFIGURATION.getCode(),
                ConfigurationCommandType.CONFIGURATION_BULK_GET.getCode(),
                (byte) (parameterOffset >> 8),
                (byte) (parameterOffset),
                (byte) (parametersCount));
    }
}
