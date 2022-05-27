package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelCapabilityReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

  private boolean endPointDynamic;
  private byte endpointId;
  private GenericDeviceClass genericDeviceClass;
  private SpecificDeviceClass specificDeviceClass;
  private CommandClass[] commandClasses;

  public MultiChannelCapabilityReport(ImmutableBuffer payload, NodeId sourceNodeId) {
    super(MultiChannelCommandType.MULTI_CHANNEL_CAPABILITY_REPORT, sourceNodeId);
    payload.skip(2);
    byte endpoint = payload.next();
    endPointDynamic = (endpoint & 0x80) != 0;
    endpointId = (byte) (endpoint & 0x7F);
    genericDeviceClass = GenericDeviceClass.ofCode(payload.next());
    specificDeviceClass = SpecificDeviceClass.ofCode(payload.next(), genericDeviceClass);

    int commandClassCount = payload.available();
    commandClasses = new CommandClass[commandClassCount];
    for (int i = 0; i < commandClassCount; i++) {
      commandClasses[i] = CommandClass.optionalOfCode(payload.next()).orElse(CommandClass.CMD_CLASS_UNKNOWN);
    }
  }
}
