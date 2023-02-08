package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SWITCH_MULTILEVEL)
public class SwitchMultilevelCommandResolver extends AbstractCommandResolver<SwitchMultiLevelCommandType> {

    private static Map<SwitchMultiLevelCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT, SwitchMultilevelReport::new);
    }

    public SwitchMultilevelCommandResolver() {
        super(suppliersPerCommandType);
    }
}
