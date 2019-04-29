package com.rposcro.jwavez.serial.frames.responses;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_PAYLOAD;

import com.rposcro.jwavez.core.model.NetworkStatistics;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.frames.ResponseFrameModel;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_NETWORK_STATS)
public class GetNetworkStatsResponse extends ZWaveResponse {

  private NetworkStatistics networkStatistics;

  public GetNetworkStatsResponse(ViewBuffer frameBuffer) {
    super(frameBuffer);
    this.networkStatistics = buildNetworkStatistics(frameBuffer);
  }

  public NetworkStatistics buildNetworkStatistics(ViewBuffer frameBuffer) {
    frameBuffer.position(FRAME_OFFSET_PAYLOAD);
    return NetworkStatistics.builder()
        .transmittedFramesCount(frameBuffer.getUnsignedWord())
        .backOffsCount(frameBuffer.getUnsignedWord())
        .receivedCorrectFramesCount(frameBuffer.getUnsignedWord())
        .lcrErrorsCount(frameBuffer.getUnsignedWord())
        .crcErrorsCount(frameBuffer.getUnsignedWord())
        .foreignHomeIdCount(frameBuffer.getUnsignedWord())
        .build();
  }
}
