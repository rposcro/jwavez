package com.rposcro.jwavez.commands.controlled;

import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.enums.SpecificDeviceClass;
import com.rposcro.jwavez.commands.enums.MultiChannelCommandType;

public class MultiChannelControlledCommand extends ControlledZWaveCommand {

  private MultiChannelControlledCommand(byte... commandPayload) {
    super(commandPayload);
  }

  public static MultiChannelControlledCommand buildGet() {
    return new MultiChannelControlledCommand(CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(), MultiChannelCommandType.MULTI_INSTANCE_GET.getCode());
  }

  public static MultiChannelControlledCommand buildEndPointGetCommand() {
    return new MultiChannelControlledCommand(CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(), MultiChannelCommandType.MULTI_CHANNEL_END_POINT_GET.getCode());
  }

  public static MultiChannelControlledCommand buildCapabilityGet(byte endpointNumber) {
    return new MultiChannelControlledCommand(
        CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
        MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_GET.getCode(),
        (byte) endpointNumber);
  }

  public static MultiChannelControlledCommand buildEndPointFind(SpecificDeviceClass searchedSpecificDevice) {
    return new MultiChannelControlledCommand(
        CommandClass.CMD_CLASS_MULTI_CHANNEL.getCode(),
        MultiChannelCommandType.MULTI_CHANNEL_END_POINT_FIND.getCode(),
        searchedSpecificDevice.getGenericDeviceClass().getCode(),
        searchedSpecificDevice.getCode());
  }
}
