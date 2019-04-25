package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType.CONFIGURATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.tools.cli.commands.AbstractSendDataBasedCommand;

public abstract class AbstractNodeConfigurationCommand extends AbstractSendDataBasedCommand {

  protected ConfigurationCommandBuilder configurationCommandBuilder;

  protected AbstractNodeConfigurationCommand() {
    configurationCommandBuilder = new ConfigurationCommandBuilder();
  }

  protected void checkConfiguration(NodeId hostNodeId, int parameterNumber, long timeout) {
    try {
      System.out.printf("Checking configuration parameter %s...\n", parameterNumber);
      ConfigurationReport report = readConfiguration(hostNodeId, parameterNumber, timeout);
      printConfigurationReport(report);
    } catch (Exception e) {
      System.out.println("Failed to read configuration due to: " + e.getMessage());
    }
  }

  protected ConfigurationReport readConfiguration(NodeId hostId, int parameterNumber, long timeout) throws SerialException {
    ZWaveSupportedCommand command = requestZWCommand(
        SendDataRequest.createSendDataRequest(
            hostId,
            configurationCommandBuilder.buildGetParameterCommand(parameterNumber),
            nextFlowId()),
        CONFIGURATION_REPORT,
        timeout);
    return (ConfigurationReport) command;
  }

  protected void printConfigurationReport(ConfigurationReport report) {
    System.out.printf(":: Report on configuration parameter %s\n", report.getParameterNumber());
    System.out.printf("  value size: %s bits\n", (8 * report.getValueSize()));
    System.out.printf("  value: %s\n", formatValue(report.getValueSize(), report.getValue()));
    System.out.println();
  }

  protected String formatValue(int size, int value) {
    switch(size) {
      case 1:
        return String.format("%02x", (byte) value);
      case 2:
        return String.format("%04x", (short) value);
      default:
        return String.format("%08x", value);
    }
  }
}
