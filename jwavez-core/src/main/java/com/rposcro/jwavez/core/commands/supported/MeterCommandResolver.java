package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.meter.MeterReport;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_METER)
public class MeterCommandResolver extends AbstractCommandResolver<MeterCommandType> {

    private static Map<MeterCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(MeterCommandType.METER_REPORT, MeterReport::new);
    }

    public MeterCommandResolver() {
        super(suppliersPerCommandType);
    }
}
