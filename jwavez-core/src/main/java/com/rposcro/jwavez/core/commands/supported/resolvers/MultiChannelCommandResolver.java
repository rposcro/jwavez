package com.rposcro.jwavez.core.commands.supported.resolvers;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelAggregatedMembersReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCapabilityReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelEndPointFindReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelEndPointReport;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_MULTI_CHANNEL)
public class MultiChannelCommandResolver extends AbstractCommandResolver<MultiChannelCommandType> {

    private static Map<MultiChannelCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(MultiChannelCommandType.MULTI_CHANNEL_END_POINT_REPORT, MultiChannelEndPointReport::new);
        suppliersPerCommandType.put(MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_REPORT, MultiChannelCapabilityReport::new);
        suppliersPerCommandType.put(MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND_REPORT, MultiChannelEndPointFindReport::new);
        suppliersPerCommandType.put(MultiChannelCommandType.MULTI_CHANNEL_AGGREGATED_MEMBERS_REPORT, MultiChannelAggregatedMembersReport::new);
        suppliersPerCommandType.put(MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP, MultiChannelCommandEncapsulation::new);
    }

    public MultiChannelCommandResolver() {
        super(suppliersPerCommandType);
    }
}
