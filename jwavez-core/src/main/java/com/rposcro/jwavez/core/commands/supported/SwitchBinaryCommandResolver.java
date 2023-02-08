package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SWITCH_BINARY)
public class SwitchBinaryCommandResolver extends AbstractCommandResolver<SwitchBinaryCommandType> {

    private static Map<SwitchBinaryCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, BinarySwitchReport::new);
    }

    public SwitchBinaryCommandResolver() {
        super(suppliersPerCommandType);
    }
}
