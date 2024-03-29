package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.model.NodeId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class ZWaveSupportedCommand<C extends CommandType> {

    protected static final int OFFSET_COMMAND_CLASS = 0;
    protected static final int OFFSET_COMMAND = 1;

    protected C commandType;
    protected NodeId sourceNodeId;
    protected byte commandVersion;

    protected ZWaveSupportedCommand(C commandType, NodeId sourceNodeId) {
        this.commandType = commandType;
        this.sourceNodeId = sourceNodeId;
    }

    public CommandClass getCommandClass() {
        return this.commandType.getCommandClass();
    }

    public String asNiceString() {
        return String.format("%s(%02x) %s(%02x) commandVersion(%02x)",
                commandType.getCommandClass(),
                commandType.getCommandClass().getCode(),
                commandType.name(),
                commandType.getCode(),
                commandVersion
        );
    }
}
