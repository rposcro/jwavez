package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.Set;

public interface ZWaveSupportedCommandResolver<T extends CommandType> {

    ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId);

    Set<T> supportedCommands();

    default CommandClass supportedCommandClass() {
        return this.getClass().getAnnotation(SupportedCommandResolver.class).commandClass();
    }
}
