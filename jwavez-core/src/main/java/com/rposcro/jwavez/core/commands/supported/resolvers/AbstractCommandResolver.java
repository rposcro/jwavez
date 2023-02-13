package com.rposcro.jwavez.core.commands.supported.resolvers;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommandResolver;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.commands.types.CommandTypesRegistry;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public abstract class AbstractCommandResolver<T extends CommandType> implements ZWaveSupportedCommandResolver<T> {

    private Map<T, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    protected AbstractCommandResolver(Map<T, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType) {
        this.suppliersPerCommandType = suppliersPerCommandType;
    }

    public Set<T> supportedCommands() {
        return suppliersPerCommandType.keySet();
    }

    @Override
    public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer, NodeId sourceNodeId) {
        T commandType = CommandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
        BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
                .orElseThrow(() -> new CommandNotSupportedException(commandType.getCommandClass(), commandType));
        return producer.apply(payloadBuffer, sourceNodeId);
    }
}
