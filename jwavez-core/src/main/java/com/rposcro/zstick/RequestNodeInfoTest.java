package com.rposcro.zstick;

import com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.frame.requests.RequestNodeInfoRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.RequestNodeInfoResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;

public class RequestNodeInfoTest {

  private SerialManager manager;
  private SerialChannel channel;

  public RequestNodeInfoTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
    this.channel.addInboundFrameInterceptor(new ApplicationUpdateCatcher());
  }

  private void test() throws Exception {
    SerialTransaction<RequestNodeInfoResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new RequestNodeInfoRequestFrame((byte) 4), RequestNodeInfoResponseFrame.class);
    TransactionResult<RequestNodeInfoResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    if (result.getStatus() == TransactionStatus.Completed) {
      System.out.println(String.format("Response successful: %s", result.getResult().isSuccessful()));
    }
  }

  public static void main(String[] args) throws Exception {
    RequestNodeInfoTest test = new RequestNodeInfoTest();
    test.test();
    Thread.sleep(30000);
    System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
