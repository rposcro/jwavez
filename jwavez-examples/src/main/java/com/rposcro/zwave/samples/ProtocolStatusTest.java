package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.probe.frame.requests.GetProtocolStatusRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetProtocolStatusResponseFrame;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;

public class ProtocolStatusTest extends AbstractExample {

  public ProtocolStatusTest() {
    super("/dev/cu.usbmodem1411");
  }

  private void checkStatus() throws Exception {
    TransactionResult<GetProtocolStatusResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetProtocolStatusRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Protocol status: %s", result.getResult().getReturnValue()));
  }

  public static void main(String[] args) throws Exception {
    ProtocolStatusTest test = new ProtocolStatusTest();
    test.checkStatus();
    System.exit(0);
  }
}
