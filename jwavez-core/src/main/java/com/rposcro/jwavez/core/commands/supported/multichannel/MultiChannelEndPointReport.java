package com.rposcro.jwavez.core.commands.supported.multichannel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.MultiChannelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MultiChannelEndPointReport extends ZWaveSupportedCommand<MultiChannelCommandType> {

  private boolean endPointsCountDynamic;
  private boolean endPointsCapabilitiesIdentical;
  private byte endpointsCount;

  public MultiChannelEndPointReport(ImmutableBuffer payload, NodeId sourceNodeId) {
    super(MultiChannelCommandType.MULTI_CHANNEL_END_POINT_REPORT, sourceNodeId);
    payload.skip(2);
    byte flags = payload.next();
    endPointsCountDynamic = (flags & 0x80) != 0;
    endPointsCapabilitiesIdentical = (flags & 0x40) != 0;
    endpointsCount = payload.next();
  }
}
