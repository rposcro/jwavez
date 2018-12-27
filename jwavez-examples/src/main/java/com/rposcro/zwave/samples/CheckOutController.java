package com.rposcro.zwave.samples;

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
import com.rposcro.jwavez.serial.transactions.TransactionResult;

public class CheckOutController  extends AbstractExample {

  public CheckOutController() {
    super("/dev/cu.usbmodem1411");
  }

  private void learnControllerCapabilities() throws Exception {
    TransactionResult<GetCapabilitiesResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetCapabilitiesRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("App version: %s", result.getResult().getSerialAppVersion()));
    System.out.println(String.format("App revision: %s", result.getResult().getSerialAppRevision()));
    System.out.println(String.format("Manufacturer id: %s", result.getResult().getManufacturerId()));
    System.out.println(String.format("Product type: %s", result.getResult().getManufacturerProductType()));
    System.out.println(String.format("Product id: %s", result.getResult().getManufacturerProductId()));
    System.out.println(String.format("Functions: %s", result.getResult().getFunctions()));
  }

  private void learnInitData() throws Exception {
    TransactionResult<GetInitDataResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetInitDataRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("Capabilities: %s", result.getResult().getCapabilities()));
    System.out.println(String.format("Chip type: %s", result.getResult().getChipType()));
    System.out.println(String.format("Chip version: %s", result.getResult().getChipVersion()));
    System.out.println(String.format("Nodes: %s", result.getResult().getNodeList()));
  }

  private void learnIds() throws Exception {
    TransactionResult<MemoryGetIdResponseFrame> result = channel.sendFrameWithResponseAndWait(new MemoryGetIdRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("HomeId: %02X", result.getResult().getHomeId()));
    System.out.println(String.format("Controller NodeId: %02X", result.getResult().getNodeId()));
  }

  private void learnVersion() throws Exception {
    TransactionResult<GetVersionResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetVersionRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("Response data: %s", result.getResult().getResponseData()));
  }

  private void learnSUCNode() throws Exception {
    TransactionResult<GetSUCNodeIdResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetSUCNodeIdRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("SUC node id: %02X", result.getResult().getSucNodeId()));
  }

  public static void main(String[] args) throws Exception {
    CheckOutController test = new CheckOutController();
    test.learnControllerCapabilities();
    test.learnInitData();
    test.learnIds();
    test.learnVersion();
    test.learnSUCNode();

    Thread.sleep(10_000);
    System.exit(0);
  }
}
