package com.rposcro.jwavez.core.commands.supported.resolvers;

import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.SceneActivationCommandType;
import com.rposcro.jwavez.core.commands.supported.sceneactivation.SceneActivationSet;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SCENE_ACTIVATION)
public class SceneActivationCommandResolver extends AbstractCommandResolver<SceneActivationCommandType> {

    private static Map<SceneActivationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(SceneActivationCommandType.SCENE_ACTIVATION_SET, SceneActivationSet::new);
    }

    public SceneActivationCommandResolver() {
        super(suppliersPerCommandType);
    }
}
