package com.rposcro.jwavez.core.commands.controlled;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.enums.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.enums.MultiChannelCommandType;

public class MultiChannelCommandBuilder {

  public ZWaveControlledCommand buildGet() {
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
}
