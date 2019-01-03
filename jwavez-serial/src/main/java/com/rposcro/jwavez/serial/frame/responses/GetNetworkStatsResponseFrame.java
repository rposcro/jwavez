package com.rposcro.jwavez.serial.frame.responses;

import com.rposcro.jwavez.core.model.NetworkStatistics;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import com.rposcro.jwavez.serial.frame.ResponseFrameModel;
import com.rposcro.jwavez.serial.frame.SOFResponseFrame;
import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import lombok.Getter;

@Getter
@ResponseFrameModel(function = SerialCommand.GET_NETWORK_STATS)
public class GetNetworkStatsResponseFrame extends SOFResponseFrame {

  private static final int OFFSET_TX_FRAMES = OFFSET_PAYLOAD;
  private static final int OFFSET_BACK_OFFS = OFFSET_PAYLOAD + 2;
  private static final int OFFSET_RX_FRAMES = OFFSET_PAYLOAD + 4;
  private static final int OFFSET_LCR_ERRORS = OFFSET_PAYLOAD + 6;
  private static final int OFFSET_CRC_ERRORS = OFFSET_PAYLOAD + 8;
  private static final int OFFSET_FOREIGN_HID = OFFSET_PAYLOAD + 10;


  private NetworkStatistics networkStatistics;

  public GetNetworkStatsResponseFrame(byte[] buffer) {
    super(buffer);
    this.networkStatistics = buildNetworkStatistics(buffer);
  }

  public NetworkStatistics buildNetworkStatistics(byte[] rawBuffer) {
    ImmutableBuffer buffer = ImmutableBuffer.overBuffer(rawBuffer, OFFSET_PAYLOAD, 12);
    return NetworkStatistics.builder()
        .transmittedFramesCount(buffer.getUnsignedWord(OFFSET_TX_FRAMES))
        .backOffsCount(buffer.getUnsignedWord(OFFSET_BACK_OFFS))
        .receivedCorrectFramesCount(buffer.getUnsignedWord(OFFSET_RX_FRAMES))
        .lcrErrorsCount(buffer.getUnsignedWord(OFFSET_LCR_ERRORS))
        .crcErrorsCount(buffer.getUnsignedWord(OFFSET_CRC_ERRORS))
        .foreignHomeIdCount(buffer.getUnsignedWord(OFFSET_FOREIGN_HID))
        .build();
  }
}
