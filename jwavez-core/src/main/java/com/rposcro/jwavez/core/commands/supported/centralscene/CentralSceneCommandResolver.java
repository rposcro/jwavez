package com.rposcro.jwavez.core.commands.supported.centralscene;

import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.resolvers.AbstractCommandResolver;
import com.rposcro.jwavez.core.commands.types.CentralSceneCommandType;
import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneSupportedReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_CENTRAL_SCENE)
public class CentralSceneCommandResolver extends AbstractCommandResolver<CentralSceneCommandType> {

    private static Map<CentralSceneCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(CentralSceneCommandType.CENTRAL_SCENE_SUPPORTED_REPORT, CentralSceneSupportedReport::new);
        suppliersPerCommandType.put(CentralSceneCommandType.CENTRAL_SCENE_NOTIFICATION, CentralSceneNotification::new);
    }

    public CentralSceneCommandResolver() {
        super(suppliersPerCommandType);
    }
}
