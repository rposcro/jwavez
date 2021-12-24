package com.rposcro.jwavez.core.commands.supported.centralscene;

import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CentralSceneSupportedReport extends ZWaveSupportedCommand<CentralSceneCommandType> {

    private short supportedScenesCount;

    public CentralSceneSupportedReport(ImmutableBuffer payload, NodeId sourceNodeId) {
        super(CentralSceneCommandType.CENTRAL_SCENE_SUPPORTED_REPORT, sourceNodeId);
        supportedScenesCount = payload.nextUnsignedByte();
    }
}
