package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;

public class MultiChannelCommandBuilder {

  public ZWaveControlledCommand buildMultiInstanceGet() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(), MultiChannelCommandType.MULTI_INSTANCE_GET.getCode());
  }

  public ZWaveControlledCommand buildEndPointGetCommand() {
    return new ZWaveControlledCommand(CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(), MultiChannelCommandType.MULTI_CHANNEL_END_POINT_GET.getCode());
  }

  public ZWaveControlledCommand buildCapabilityGet(byte endpointNumber) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
        MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_GET.getCode(),
        (byte) endpointNumber);
  }

  public ZWaveControlledCommand buildEndPointFind(SpecificDeviceClass searchedSpecificDevice) {
    return new ZWaveControlledCommand(
        CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
        MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND.getCode(),
        searchedSpecificDevice.getGenericDeviceClass().getCode(),
        searchedSpecificDevice.getCode());
  }

  public ZWaveControlledCommand encapsulateCommand(byte sourceEndpoint, byte destinationEndpoint, ZWaveControlledCommand command) {
    byte[] payload = new byte[4 + command.getPayload().getLength()];
    payload[0] = CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode();
    payload[1] = MultiChannelCommandType.MULTI_CHANNEL_CMD_ENCAP.getCode();
    payload[2] = sourceEndpoint;
    payload[3] = destinationEndpoint;
    command.getPayload().cloneBytes(payload, 4);
    return new ZWaveControlledCommand(payload);
  }
}
