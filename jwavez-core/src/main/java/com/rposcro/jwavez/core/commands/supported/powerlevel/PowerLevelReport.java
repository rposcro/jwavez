package com.rposcro.jwavez.core.commands.supported.powerlevel;

import com.rposcro.jwavez.core.classes.CommandClassVersion;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PowerLevelReport extends ZWaveSupportedCommand<PowerLevelCommandType> {

    private CommandClassVersion version;
    private short powerLevel;
    private short timeout;

    public PowerLevelReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(PowerLevelCommandType.POWER_LEVEL_REPORT, sourceNodeId);
        version = CommandClassVersion.V1;
        payload.skip(2);
        powerLevel = payload.nextUnsignedByte();
        timeout = payload.nextUnsignedByte();
    }
}
