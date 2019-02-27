package com.rposcro.jwavez.tools.cli.commands.dongle;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.GetCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetControllerCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetInitDataRequest;
import com.rposcro.jwavez.serial.frames.requests.GetLibraryTypeRequest;
import com.rposcro.jwavez.serial.frames.requests.GetRFPowerLevelRequest;
import com.rposcro.jwavez.serial.frames.requests.GetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.requests.GetVersionRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetLibraryTypeResponse;
import com.rposcro.jwavez.serial.frames.responses.GetRFPowerLevelResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.GetVersionResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.commands.AbstractSyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.DongleCheckOptions;
import com.rposcro.jwavez.tools.cli.utils.SerialExecutor;
import java.util.stream.Collectors;

public class DongleCheckCommand extends AbstractSyncBasedCommand {

  private DongleCheckOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DongleCheckOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    connect(options);
    System.out.println("Checking dongle " + options.getDevice() + "...\n");
    runCheck(this::runNetworkIds, options.runNetworkIds(), "Network IDs");
    runCheck(this::runSUCId, options.runSucId(), "SUC Id");
    runCheck(this::runControllerCapabilities, options.runControllerCapabilities(), "Controller Capabilities");
    runCheck(this::runCapabilities, options.runCapabilities(), "Device Capabilities");
    runCheck(this::runInitialData, options.runInitialData(), "Initial Data");
    runCheck(this::runGetVersion, options.runGetVersion(), "Version");
    runCheck(this::runLibraryType, options.runLibraryType(), "Library Type");
    runCheck(this::runPowerLevel, options.runPowerLevel(), "Power Level");
  }

  private void runCheck(SerialExecutor executor, boolean activate, String header) {
    if (activate) {
      sectionHeader(header);
      try {
        executor.execute();
      } catch(SerialException e) {
        sectionFailure();
      }
      sectionFooter();
      System.out.println();
    }
  }

  private void runSUCId() throws FlowException {
    GetSUCNodeIdResponse response = controller.requestResponseFlow(GetSUCNodeIdRequest.createGetSUCNodeIdRequest());
    System.out.printf("  SUC node id: %02X\n", response.getSucNodeId().getId());
  }

  private void runPowerLevel() throws FlowException {
    GetRFPowerLevelResponse response = controller.requestResponseFlow(GetRFPowerLevelRequest.createGetRFPowerLevelRequest());
    System.out.printf("  Power level: %s\n", response.getPowerLevel());
  }

  private void runNetworkIds() throws FlowException {
    MemoryGetIdResponse response = controller.requestResponseFlow(MemoryGetIdRequest.createMemoryGetIdRequest());
    System.out.printf("  HomeId: %04x\n", response.getHomeId());
    System.out.printf("  Dongle NodeId: %02x\n", response.getNodeId().getId());
  }

  private void runLibraryType() throws FlowException {
    GetLibraryTypeResponse response = controller.requestResponseFlow(GetLibraryTypeRequest.createLibraryTypeRequest());
    System.out.printf("  Library type: %s\n", response.getLibraryType());
  }

  private void runInitialData() throws FlowException {
    GetInitDataResponse response = controller.requestResponseFlow(GetInitDataRequest.createGetInitDataRequest());
    System.out.printf("  Version: %s\n", response.getVersion());
    System.out.printf("  Capabilities: %s\n", response.getCapabilities());
    System.out.printf("  Chip type: %s\n", response.getChipType());
    System.out.printf("  Chip version: %s\n", response.getChipVersion());
    System.out.printf("  Nodes: %s\n", response.getNodes().stream()
      .map(NodeId::getId)
      .collect(Collectors.toList()));
  }

  private void runGetVersion() throws FlowException {
    GetVersionResponse response = controller.requestResponseFlow(GetVersionRequest.createGetVersionRequest());
    System.out.printf("  Version: %s\n", response.getVersion());
    System.out.printf("  ZWaveResponse data: %s\n", response.getResponseData());
  }

  private void runControllerCapabilities() throws FlowException {
    GetControllerCapabilitiesResponse response = controller.requestResponseFlow(GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest());
    System.out.printf("  Is real primary: %s\n", response.isRealPrimary());
    System.out.printf("  Is secondary: %s\n", response.isSecondary());
    System.out.printf("  Is SUC: %s\n", response.isSUC());
    System.out.printf("  Is SIS: %s\n", response.isSIS());
    System.out.printf("  Is on another network: %s\n", response.isOnOtherNetwork());
  }

  private void runCapabilities() throws FlowException {
    GetCapabilitiesResponse response = controller.requestResponseFlow(GetCapabilitiesRequest.createGetCapabilitiesRequest());
    System.out.printf("  App version: %s\n", response.getSerialAppVersion());
    System.out.printf("  App revision: %s\n", response.getSerialAppRevision());
    System.out.printf("  Manufacturer id: %s\n", response.getManufacturerId());
    System.out.printf("  Product type: %s\n", response.getManufacturerProductType());
    System.out.printf("  Product id: %s\n", response.getManufacturerProductId());
    System.out.printf("  Functions: %s\n", response.getSerialCommands().stream()
        .map(code -> {
          try {
            return SerialCommand.ofCode(code.byteValue());
          } catch(IllegalArgumentException e) {
            return String.format("UNKNOWN(%02x)", code.byteValue());
          }})
        .collect(Collectors.toList()));
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

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("info", "-d", "/dev/tty.usbmodem1421");
  }
}
