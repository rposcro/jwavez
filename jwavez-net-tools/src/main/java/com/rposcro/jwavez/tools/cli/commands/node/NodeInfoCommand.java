package com.rposcro.jwavez.tools.cli.commands.node;

import com.rposcro.jwavez.core.commands.controlled.builders.ManufacturerSpecificCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.VersionCommandBuilder;
import com.rposcro.jwavez.core.commands.types.ManufacturerSpecificCommandType;
import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.manufacturerspecific.ManufacturerSpecificReport;
import com.rposcro.jwavez.core.commands.supported.version.VersionCommandClassReport;
import com.rposcro.jwavez.core.commands.supported.version.VersionReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeInfo;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationUpdateCallback;
import com.rposcro.jwavez.serial.frames.requests.RequestNodeInfoRequest;
import com.rposcro.jwavez.serial.model.ApplicationUpdateStatus;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.commands.AbstractAsyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.NodeInfoOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeInfoCommand extends AbstractAsyncBasedCommand {

  private NodeInfoOptions options;
  private NodeInfoReport nodeInfoReport;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NodeInfoOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("Starting node info check command ...");
    nodeInfoReport = new NodeInfoReport();
    ProcedureUtil.executeProcedure(this::runFetch);
    System.out.println("Node info check command finished");
  }

  private void runFetch() throws SerialException {
    connect(options);
    runFetchClassInfo();
    if (options.isCheckCommandsVersions()) {
      runFetchClassVersionInfo();
    }
    if (options.isCheckProtocolVersions()) {
      runFetchProtocolVersionInfo();
    }
    if (options.isManufacturerSpecificInfo()) {
      runFetchManufacturerSpecificInfo();
    }
    printReport();
  }

  private void printReport() {
    NodeInfo nodeInfo = nodeInfoReport.nodeInfo;
    if (nodeInfo != null) {
      List<String> commandClasses = Arrays.stream(nodeInfo.getCommandClasses())
              .map(clazz -> clazz.toString())
              .collect(Collectors.toList());
      StringBuffer logMessage = new StringBuffer("Node info successfully received\n")
              .append(String.format("  node id: %02X\n", nodeInfo.getId().getId()))
              .append(String.format("  basic dongleDevice class: %s\n", nodeInfo.getBasicDeviceClass()))
              .append(String.format("  generic dongleDevice class: %s\n", nodeInfo.getGenericDeviceClass()))
              .append(String.format("  specific dongleDevice class: %s\n", nodeInfo.getSpecificDeviceClass()))
              .append(String.format("  command classes: %s\n", String.join(", ", commandClasses)));
      System.out.println(logMessage.toString());

      if (nodeInfoReport.classVersionReports != null) {
        System.out.println("Class versions:");
        for (VersionCommandClassReport report: nodeInfoReport.classVersionReports) {
          System.out.println("  " + report.getCommandClass() + ": " + report.getCommandClassVersion());
        }
        System.out.println();
      }

      if (nodeInfoReport.protocolVersionReport != null) {
        System.out.println("Protocol versions:");
        System.out.println("  ZWave Library Type: " + nodeInfoReport.protocolVersionReport.getZWaveLibraryTypeEnum());
        System.out.println("  Protocol Version: " + nodeInfoReport.protocolVersionReport.getZWaveProtocolVersion());
        System.out.println("  Protocol Sub Version: " + nodeInfoReport.protocolVersionReport.getZWaveProtocolSubVersion());
        System.out.println("  Application Version: " + nodeInfoReport.protocolVersionReport.getApplicationVersion());
        System.out.println("  Application Sub Version: " + nodeInfoReport.protocolVersionReport.getApplicationSubVersion());
        System.out.println();
      }

      if (nodeInfoReport.manufacturerSpecificReport != null) {
        System.out.println("Manufacturer specific:");
        System.out.println("  Manufacturer Id: " + String.format("0x%04X", nodeInfoReport.manufacturerSpecificReport.getManufacturerId()));
        System.out.println("  Product Type Id: " + String.format("0x%04X", nodeInfoReport.manufacturerSpecificReport.getProductTypeId()));
        System.out.println("  Product Id: " + String.format("0x%04X", nodeInfoReport.manufacturerSpecificReport.getProductId()));
      }
    } else {
      System.out.println("Node Class Info was not fetched!");
    }
  }

  private void runFetchClassInfo() throws SerialException {
    System.out.println("Fetching class info");
    ApplicationUpdateCallback callback = requestZWCallback(
            RequestNodeInfoRequest.createRequestNodeInfoRequest(options.getNodeId()),
            SerialCommand.APPLICATION_UPDATE,
            options.getTimeout()
    );
    nodeInfoReport.nodeInfo = callback.getNodeInfo();
    nodeInfoReport.applicationUpdateStatus = callback.getStatus();
  }

  private void runFetchClassVersionInfo() throws SerialException {
    System.out.println("Fetching class version info");
    nodeInfoReport.classVersionReports = new VersionCommandClassReport[nodeInfoReport.nodeInfo.getCommandClasses().length];
    int idx = 0;
    for (CommandClass commandClass: nodeInfoReport.nodeInfo.getCommandClasses()) {
      VersionCommandClassReport report = requestApplicationCommand(
              options.getNodeId(),
              new VersionCommandBuilder().buildCommandClassGetCommand(commandClass),
              VersionCommandType.VERSION_COMMAND_CLASS_REPORT,
              options.getTimeout());
      nodeInfoReport.classVersionReports[idx++] = report;
    }
  }

  private void runFetchProtocolVersionInfo() throws SerialException {
    System.out.println("Fetching protocols version info");
    VersionReport report = requestApplicationCommand(
            options.getNodeId(),
            new VersionCommandBuilder().buildGetCommand(),
            VersionCommandType.VERSION_REPORT,
            options.getTimeout());
    nodeInfoReport.protocolVersionReport = report;
  }

  private void runFetchManufacturerSpecificInfo() throws SerialException {
    System.out.println("Fetching manufacturer specific info");
    ManufacturerSpecificReport report = requestApplicationCommand(
            options.getNodeId(),
            new ManufacturerSpecificCommandBuilder().buildGetCommand(),
            ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_REPORT,
            options.getTimeout());
    nodeInfoReport.manufacturerSpecificReport = report;
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "info", "-d", "/dev/tty.usbmodem14201", "-n", "3", "-t", "5000", "-vp", "-vc", "-ms");
  }

  public static class NodeInfoReport {
    private ApplicationUpdateStatus applicationUpdateStatus;
    private NodeInfo nodeInfo;
    private VersionReport protocolVersionReport;
    private VersionCommandClassReport[] classVersionReports;
    private ManufacturerSpecificReport manufacturerSpecificReport;
  }
}
