package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.powerlevel.PowerLevelReport;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_POWERLEVEL)
public class PowerLevelCommandResolver extends AbstractCommandResolver<PowerLevelCommandType> {

    private static Map<PowerLevelCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(PowerLevelCommandType.POWER_LEVEL_REPORT, PowerLevelReport::new);
    }

    public PowerLevelCommandResolver() {
        super(suppliersPerCommandType);
    }
}
