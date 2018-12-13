package com.rposcro.zstick;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.frame.requests.EnableSUCRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.EnableSUCResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

public class SUCTest {

  private SerialManager manager;
  private SerialChannel channel;

  public SUCTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
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
//    System.out.println(String.format("SUC Node Id: %s", result.getResult()));
  }

  public static void main(String[] args) throws Exception {
    SUCTest test = new SUCTest();
    test.checkSUCNode();
  //  test.enableSUCMode();
    System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
