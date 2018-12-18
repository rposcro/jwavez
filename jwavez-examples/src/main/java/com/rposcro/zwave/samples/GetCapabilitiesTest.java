package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.frame.requests.GetCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

public class GetCapabilitiesTest {

  private SerialManager manager;
  private SerialChannel channel;

  public GetCapabilitiesTest() {
    this.manager = new SerialManager("/dev/cu.usbmodem1411");
    this.channel = manager.connect();
  }

  private void testCapabilities() throws Exception {
    SerialTransaction<GetCapabilitiesResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new GetCapabilitiesRequestFrame(), GetCapabilitiesResponseFrame.class);
    TransactionResult<GetCapabilitiesResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    System.out.println(String.format("App version: %s", result.getResult().getSerialAppVersion()));
    System.out.println(String.format("App revision: %s", result.getResult().getSerialAppRevision()));
    System.out.println(String.format("Manufacturer id: %s", result.getResult().getManufacturerId()));
    System.out.println(String.format("Product type: %s", result.getResult().getManufacturerProductType()));
    System.out.println(String.format("Product id: %s", result.getResult().getManufacturerProductId()));
    System.out.println(String.format("Functions: %s", result.getResult().getFunctions()));
  }

  private void testInitData() throws Exception {
    SerialTransaction<GetInitDataResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new GetInitDataRequestFrame(), GetInitDataResponseFrame.class);
    TransactionResult<GetInitDataResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", transaction.status()));
    System.out.println(String.format("Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("Capabilities: %s", result.getResult().getCapabilities()));
    System.out.println(String.format("Chip type: %s", result.getResult().getChipType()));
    System.out.println(String.format("Chip version: %s", result.getResult().getChipVersion()));
    System.out.println(String.format("Nodes: %s", result.getResult().getNodes()));
  }

  public static void main(String[] args) throws Exception {
    GetCapabilitiesTest test = new GetCapabilitiesTest();
    test.testCapabilities();
    test.testInitData();
    Thread.sleep(65000);
    System.exit(0);
  }

  private static String orNull(byte[] array) {
    return array == null ? "null" : "" + array[0];
  }
}
