package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.frame.requests.EnableSUCRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.EnableSUCResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

/**
 * Checks SUC mode on controller stick.
 */
public class SUCTest extends AbstractExample {

  public SUCTest() {
    super("/dev/cu.usbmodem1411");
  }

  private void checkSUCNode() throws Exception {
    SerialTransaction<GetSUCNodeIdResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new GetSUCNodeIdRequestFrame(), GetSUCNodeIdResponseFrame.class);
    TransactionResult<GetSUCNodeIdResponseFrame> gvrFrame = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    System.out.println(String.format("SUC Node Id: %s", gvrFrame.getResult().getSucNodeId()));
  }

  private void enableSUCMode() throws Exception {
    SerialTransaction<EnableSUCResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new EnableSUCRequestFrame(), EnableSUCResponseFrame.class);
    TransactionResult<EnableSUCResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
  }

  public static void main(String[] args) throws Exception {
    SUCTest test = new SUCTest();
    test.checkSUCNode();
  //  test.enableSUCMode();
    System.exit(0);
  }
}
