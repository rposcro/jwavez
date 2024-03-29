package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.resolvers.AbstractCommandResolver;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_CONFIGURATION)
public class ConfigurationCommandResolver extends AbstractCommandResolver<ConfigurationCommandType> {

    private static Map<ConfigurationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(ConfigurationCommandType.CONFIGURATION_REPORT, ConfigurationReport::new);
    }

    public ConfigurationCommandResolver() {
        super(suppliersPerCommandType);
    }
}
