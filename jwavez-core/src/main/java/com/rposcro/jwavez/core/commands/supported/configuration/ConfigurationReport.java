package com.rposcro.jwavez.core.commands.supported.configuration;

import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConfigurationReport extends ZWaveSupportedCommand<ConfigurationCommandType> {

  private short parameterNumber;
  private byte valueSize;
  private int value;

  public ConfigurationReport(ImmutableBuffer payload, NodeId sourceNodeId) {
    super(ConfigurationCommandType.CONFIGURATION_REPORT, sourceNodeId);
    parameterNumber = payload.getUnsignedByte(2);
    valueSize = (byte) (payload.getByte(3) & 0x07);

    value = 0;
    for (int i = 0; i < valueSize; i++) {
      value <<= 8;
      value |= (payload.getUnsignedByte(4 + i));
    }
  }
}
