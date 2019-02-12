package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import com.rposcro.jwavez.serial.probe.frame.requests.GetCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetControllerCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetRFPowerLevelRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetTypeLibraryRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetVersionRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetControllerCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetLibraryTypeResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetRFPowerLevelResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetVersionResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.jwavez.serial.probe.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DongleCheckCommand extends AbstractDeviceCommand {

  private DongleCheckOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DongleCheckOptions(args);
  }

  @Override
  public void execute() {
    connect(options);
    System.out.println("Checking dongle " + options.getDevice() + "...");
    runCheck(this::runNetworkIds, options.runNetworkIds(), "Network IDs");
    runCheck(this::runSUCId, options.runSucId(), "SUC Id");
    runCheck(this::runControllerCapabilities, options.runControllerCapabilities(), "Controller Capabilities");
    runCheck(this::runCapabilities, options.runCapabilities(), "Device Capabilities");
    runCheck(this::runInitialData, options.runInitialData(), "Initial Data");
    runCheck(this::runGetVersion, options.runGetVersion(), "Version");
    runCheck(this::runLibraryType, options.runLibraryType(), "Library Type");
    runCheck(this::runPowerLevel, options.runPowerLevel(), "Power Level");
  }

  private void runCheck(Supplier<Boolean> executor, boolean activate, String header) {
    if (activate) {
      sectionHeader(header);
      if (!executor.get()) {
        sectionFailure();
      };
      sectionFooter();
      System.out.println();
    }
  }

  private boolean runSUCId() {
    TransactionResult<GetSUCNodeIdResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetSUCNodeIdRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  SUC node id: %02X", result.getResult().getSucNodeId()));
    return true;
  }

  private boolean runPowerLevel() {
    TransactionResult<GetRFPowerLevelResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetRFPowerLevelRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  Power level: %s", result.getResult().getPowerLevel()));
    return true;
  }

  private boolean runNetworkIds() {
    TransactionResult<MemoryGetIdResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new MemoryGetIdRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  HomeId: %02x", result.getResult().getHomeId()));
    System.out.println(String.format("  Dongle NodeId: %02x", result.getResult().getNodeId().getId()));
    return true;
  }

  private boolean runLibraryType() {
    TransactionResult<GetLibraryTypeResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetTypeLibraryRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  Library type: %s", result.getResult().getLibraryType()));
    return true;
  }

  private boolean runInitialData() {
    TransactionResult<GetInitDataResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetInitDataRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("  Capabilities: %s", result.getResult().getCapabilities()));
    System.out.println(String.format("  Chip type: %s", result.getResult().getChipType()));
    System.out.println(String.format("  Chip version: %s", result.getResult().getChipVersion()));
    System.out.println(String.format("  Nodes: %s", result.getResult().getNodeList().stream()
      .map(NodeId::getId)
      .collect(Collectors.toList())));
    return true;
  }

  private boolean runGetVersion() {
    TransactionResult<GetVersionResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetVersionRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  Version: %s", result.getResult().getVersion()));
    System.out.println(String.format("  ZWaveResponse data: %s", result.getResult().getResponseData()));
    return true;
  }

  private boolean runControllerCapabilities() {
    TransactionResult<GetControllerCapabilitiesResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetControllerCapabilitiesRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    GetControllerCapabilitiesResponseFrame frame = result.getResult();
    System.out.println(String.format("  Is real primary: %s", frame.isRealPrimary()));
    System.out.println(String.format("  Is secondary: %s", frame.isSecondary()));
    System.out.println(String.format("  Is SUC: %s", frame.isSUC()));
    System.out.println(String.format("  Is SIS: %s", frame.isSIS()));
    System.out.println(String.format("  Is on another network: %s", frame.isOnOtherNetwork()));
    return true;
  }

  private boolean runCapabilities() {
    TransactionResult<GetCapabilitiesResponseFrame> result = serialChannel.sendFrameWithResponseAndWait(new GetCapabilitiesRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      return false;
    }
    System.out.println(String.format("  App version: %s", result.getResult().getSerialAppVersion()));
    System.out.println(String.format("  App revision: %s", result.getResult().getSerialAppRevision()));
    System.out.println(String.format("  Manufacturer id: %s", result.getResult().getManufacturerId()));
    System.out.println(String.format("  Product type: %s", result.getResult().getManufacturerProductType()));
    System.out.println(String.format("  Product id: %s", result.getResult().getManufacturerProductId()));
    System.out.println(String.format("  Functions: %s", result.getResult().getFunctions().stream()
        .map(code -> {
          try {
            return SerialCommand.ofCode(code.byteValue());
          } catch(IllegalArgumentException e) {
            return String.format("UNKNOWN(%02x)", code.byteValue());
          }})
        .collect(Collectors.toList())));
    return true;
  }

  private void sectionHeader(String sectionName) {
    System.out.println(":: " + sectionName);
  }

  private void sectionFooter() {
    //System.out.println(":: ");
  }

  private void sectionFailure() {
    System.out.println("!! Error occurred");
  }
}
