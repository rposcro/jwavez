package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.rposcro.jwavez.core.commands.supported.basic.BasicSet;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_BASIC)
public class BasicCommandResolver extends AbstractCommandResolver<BasicCommandType> {

    private static Map<BasicCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(BasicCommandType.BASIC_SET, BasicSet::new);
    }

    public BasicCommandResolver() {
        super(suppliersPerCommandType);
    }
}
