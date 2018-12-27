package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.frame.requests.EnableSUCRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.EnableSUCResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

/**
 * Checks SUC mode on controller stick.
 */
public class SUCTest extends AbstractExample {

  public SUCTest() {
    super("/dev/cu.usbmodem1411");
  }

  private void checkSUCNode() throws Exception {
    TransactionResult<GetSUCNodeIdResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetSUCNodeIdRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("SUC Node Id: %s", result.getResult().getSucNodeId()));
  }

  private void enableSUCMode() throws Exception {
    TransactionResult<EnableSUCResponseFrame> result = channel.sendFrameWithResponseAndWait(new EnableSUCRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
  }

  public static void main(String[] args) throws Exception {
    SUCTest test = new SUCTest();
    test.checkSUCNode();
  //  test.enableSUCMode();
    System.exit(0);
  }
}
