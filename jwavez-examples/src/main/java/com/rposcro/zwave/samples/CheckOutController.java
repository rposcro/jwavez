package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.factory.SerialChannelManager;
import com.rposcro.jwavez.serial.frame.requests.GetCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetVersionRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetVersionResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;

public class CheckOutController  extends AbstractExample {

  public CheckOutController() {
    super("/dev/cu.usbmodem1411");
  }

  private void learnControllerCapabilities() throws Exception {
    SerialTransaction<GetCapabilitiesResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new GetCapabilitiesRequestFrame(), GetCapabilitiesResponseFrame.class);
    TransactionResult<GetCapabilitiesResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("App version: %s", result.getResult().getSerialAppVersion()));
    System.out.println(String.format("App revision: %s", result.getResult().getSerialAppRevision()));
    System.out.println(String.format("Manufacturer id: %s", result.getResult().getManufacturerId()));
    System.out.println(String.format("Product type: %s", result.getResult().getManufacturerProductType()));
    System.out.println(String.format("Product id: %s", result.getResult().getManufacturerProductId()));
    System.out.println(String.format("Functions: %s", result.getResult().getFunctions()));
  }

  private void learnInitData() throws Exception {
    SerialTransaction<GetInitDataResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
      new GetInitDataRequestFrame(), GetInitDataResponseFrame.class);
    TransactionResult<GetInitDataResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("Capabilities: %s", result.getResult().getCapabilities()));
    System.out.println(String.format("Chip type: %s", result.getResult().getChipType()));
    System.out.println(String.format("Chip version: %s", result.getResult().getChipVersion()));
    System.out.println(String.format("Nodes: %s", result.getResult().getNodeList()));
  }

  private void learnIds() throws Exception {
    SerialTransaction<MemoryGetIdResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new MemoryGetIdRequestFrame(), MemoryGetIdResponseFrame.class);
    TransactionResult<MemoryGetIdResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("HomeId: %02X", result.getResult().getHomeId()));
    System.out.println(String.format("Controller NodeId: %02X", result.getResult().getNodeId()));
  }

  private void learnVersion() throws Exception {
    SerialTransaction<GetVersionResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new GetVersionRequestFrame(), GetVersionResponseFrame.class);
    TransactionResult<GetVersionResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("Response data: %s", result.getResult().getResponseData()));
  }

  private void learnSUCNode() throws Exception {
    SerialTransaction<GetSUCNodeIdResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        new GetSUCNodeIdRequestFrame(), GetSUCNodeIdResponseFrame.class);
    TransactionResult<GetSUCNodeIdResponseFrame> result = channel.executeTransaction(transaction).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("SUC node id: %02X", result.getResult().getSucNodeId()));
  }

  public static void main(String[] args) throws Exception {
    CheckOutController test = new CheckOutController();
    test.learnControllerCapabilities();
    test.learnInitData();
    test.learnIds();
    test.learnVersion();

    Thread.sleep(10_000);
    System.exit(0);
  }
}
