package com.rposcro.zwave.samples;

import com.rposcro.jwavez.core.model.NetworkStatistics;
import com.rposcro.jwavez.serial.probe.frame.requests.GetNetworkStatsRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetNetworkStatsResponseFrame;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.jwavez.serial.probe.transactions.TransactionStatus;

public class NetworkStatisticsTest extends AbstractExample {

  public NetworkStatisticsTest() {
    super("/dev/cu.usbmodem1411");
  }

  private void checkStatistics() throws Exception {
    TransactionResult<GetNetworkStatsResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetNetworkStatsRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    if (result.getStatus() == TransactionStatus.Completed) {
      NetworkStatistics stats = result.getResult().getNetworkStatistics();
      System.out.println(String.format("Tx Count: %s", stats.getTransmittedFramesCount()));
      System.out.println(String.format("Rx Count: %s", stats.getReceivedCorrectFramesCount()));
      System.out.println(String.format("Back Offs Count: %s", stats.getBackOffsCount()));
      System.out.println(String.format("LCR Errors Count: %s", stats.getLcrErrorsCount()));
      System.out.println(String.format("CRC Errors Count: %s", stats.getCrcErrorsCount()));
      System.out.println(String.format("Foreign Home Ids Count: %s", stats.getForeignHomeIdCount()));
    }
  }

  public static void main(String[] args) throws Exception {
    NetworkStatisticsTest test = new NetworkStatisticsTest();
    test.checkStatistics();
    System.exit(0);
  }
}
