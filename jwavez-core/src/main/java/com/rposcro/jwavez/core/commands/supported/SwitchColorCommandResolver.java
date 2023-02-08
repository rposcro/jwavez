package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorSupportedReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SWITCH_COLOR)
public class SwitchColorCommandResolver extends AbstractCommandResolver<SwitchColorCommandType> {

    private static Map<SwitchColorCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(SwitchColorCommandType.SWITCH_COLOR_SUPPORTED_REPORT, SwitchColorSupportedReport::new);
        suppliersPerCommandType.put(SwitchColorCommandType.SWITCH_COLOR_REPORT, SwitchColorReport::new);
    }

    public SwitchColorCommandResolver() {
        super(suppliersPerCommandType);
    }
}
