package com.rposcro.jwavez.core.commands.supported.notification;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.resolvers.AbstractCommandResolver;
import com.rposcro.jwavez.core.commands.types.NotificationCommandType;
import com.rposcro.jwavez.core.model.NodeId;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_NOTIFICATION)
public class NotificationCommandResolver extends AbstractCommandResolver<NotificationCommandType> {

    private static Map<NotificationCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(NotificationCommandType.NOTIFICATION_REPORT, NotificationReport::new);
    }

    public NotificationCommandResolver() {
        super(suppliersPerCommandType);
    }
}
