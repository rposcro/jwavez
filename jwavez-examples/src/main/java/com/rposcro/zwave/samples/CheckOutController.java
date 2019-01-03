package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.frame.requests.GetCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetRFPowerLevelRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetTypeLibraryRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.GetVersionRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetLibraryTypeResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetRFPowerLevelResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.GetVersionResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import java.util.stream.Collectors;

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
    System.out.println(String.format("Functions: %s", result.getResult().getFunctions().stream()
    .map(code -> {
      try {
        return SerialCommand.ofCode(code.byteValue());
      } catch(IllegalArgumentException e) {
        return String.format("UNKNOWN(%02x)", code.byteValue());
      }})
    .collect(Collectors.toList())));
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
    System.out.println(String.format("HomeId: %02x", result.getResult().getHomeId()));
    System.out.println(String.format("Controller NodeId: %02x", result.getResult().getNodeId().getId()));
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

  private void learnPowerLevel() throws Exception {
    TransactionResult<GetRFPowerLevelResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetRFPowerLevelRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Power level: %s", result.getResult().getPowerLevel()));
  }

  private void learnLibraryType() throws Exception {
    TransactionResult<GetLibraryTypeResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetTypeLibraryRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("Library type: %s", result.getResult().getLibraryType()));
  }

  public static void main(String[] args) throws Exception {
    CheckOutController test = new CheckOutController();
    test.learnControllerCapabilities();
//    test.learnInitData();
//    test.learnIds();
//    test.learnVersion();
//    test.learnSUCNode();
//    test.learnPowerLevel();
//    test.learnLibraryType();

    Thread.sleep(10_000);
    System.exit(0);
  }
}
