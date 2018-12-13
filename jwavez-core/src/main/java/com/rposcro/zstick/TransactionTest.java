package com.rposcro.zstick;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.frame.requests.GetVersionRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetVersionResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

public class TransactionTest {

  private SerialManager manager;
  private SerialChannel channel;

  public TransactionTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
  }

  private void version() throws Exception {
    SerialTransaction<GetVersionResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new GetVersionRequestFrame(), GetVersionResponseFrame.class);
    TransactionResult<GetVersionResponseFrame> gvrFrame = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    System.out.println(String.format("Version: %s", gvrFrame.getResult().getVersion()));
  }

  private void memoryGet() throws Exception {
    SerialTransaction<MemoryGetIdResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new MemoryGetIdRequestFrame(), MemoryGetIdResponseFrame.class);
    TransactionResult<MemoryGetIdResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    System.out.println(String.format("HomeId: %h, nodeId: %s", result.getResult().getHomeId(), result.getResult().getNodeId()));
  }

  public static void main(String[] args) throws Exception {
    TransactionTest test = new TransactionTest();
    test.version();
    test.memoryGet();
    System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
