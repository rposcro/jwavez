package com.rposcro.jwavez.core.commands.controlled.builders.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class MultiChannelCommandBuilderV3 {

    public ZWaveControlledCommand buildEndPointGetCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
                MultiChannelCommandType.MULTI_CHANNEL_END_POINT_GET.getCode());
    }

    public ZWaveControlledCommand buildCapabilityGet(byte endpointNumber) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
                MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_GET.getCode(),
                endpointNumber);
    }

    public ZWaveControlledCommand buildEndPointFind(SpecificDeviceClass specificDevice) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
                MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND.getCode(),
                specificDevice.getGenericDeviceClass().getCode(),
                specificDevice.getCode());
    }

    public ZWaveControlledCommand encapsulateCommand(
            byte sourceEndpoint, byte destinationEndpoint, ZWaveControlledCommand command) {
        byte[] payload = new byte[4 + command.getPayload().getLength()];
        payload[0] = CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode();
        payload[1] = MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP.getCode();
        payload[2] = sourceEndpoint;
        payload[3] = destinationEndpoint;
        command.getPayload().cloneBytes(payload, 4);
        return new ZWaveControlledCommand(payload);
    }
}
