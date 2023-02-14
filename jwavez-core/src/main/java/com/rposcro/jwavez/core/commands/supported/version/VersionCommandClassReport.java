package com.rposcro.jwavez.core.commands.supported.version;

import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
public class VersionCommandClassReport extends ZWaveSupportedCommand<VersionCommandType> {

    private final byte reportedCommandClass;
    private final short reportedCommandClassVersion;

    public VersionCommandClassReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(VersionCommandType.VERSION_COMMAND_CLASS_REPORT, sourceNodeId);
        payload.rewind().skip(2);
        reportedCommandClass = payload.nextByte();
        reportedCommandClassVersion = payload.nextUnsignedByte();
    }

    public CommandClass getDecodedReportedCommandClass() {
        return CommandClass.optionalOfCode(reportedCommandClass).orElse(null);
    }
}
