package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.types.ConfigurationCommandType.CONFIGURATION_REPORT;

import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.cli.commands.AbstractAsyncBasedCommand;

public abstract class AbstractNodeConfigurationCommand extends AbstractAsyncBasedCommand {

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
        ZWaveSupportedCommand command = requestApplicationCommand(
                transportRequestBuilder.createSendDataRequest(
                        hostId,
                        configurationCommandBuilder.v1().buildGetParameterCommand(parameterNumber),
                        nextFlowId()),
                CONFIGURATION_REPORT,
                timeout);
        return (ConfigurationReport) command;
    }

    protected void printConfigurationReport(ConfigurationReport report) {
        System.out.printf(":: Report on configuration parameter %s\n", report.getParameterNumber());
        System.out.printf("  value size: %s bits\n", (8 * report.getParameterSize()));
        System.out.printf("  value: %s\n", formatValue(report.getParameterSize(), report.getParameterValue()));
        System.out.println();
    }

    protected String formatValue(int size, long value) {
        switch (size) {
            case 1:
                return String.format("%02x", (byte) value);
            case 2:
                return String.format("%04x", (short) value);
            default:
                return String.format("%08x", value);
        }
    }
}
