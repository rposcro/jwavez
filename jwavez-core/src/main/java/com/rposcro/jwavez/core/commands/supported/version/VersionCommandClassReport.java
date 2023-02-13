package com.rposcro.jwavez.core.commands.supported.version;

import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VersionCommandClassReport extends ZWaveSupportedCommand<VersionCommandType> {

    private final CommandClass commandClass;
    private final short commandClassVersion;

    public VersionCommandClassReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(VersionCommandType.VERSION_COMMAND_CLASS_REPORT, sourceNodeId);
        payload.skip(2);
        commandClass = CommandClass.ofCode(payload.nextByte());
        commandClassVersion = payload.nextUnsignedByte();
    }
}
