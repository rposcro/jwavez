package com.rposcro.jwavez.samples.fibaro;

import static com.rposcro.jwavez.serial.frames.requests.SendDataRequest.createSendDataRequest;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.enums.AssociationCommandType;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.responses.SendDataResponse;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinaryCheckOut extends AbstractExample implements AutoCloseable {

  private final NodeId addresseeId;
  private final GeneralAsynchronousController controller;
  private byte callbackFunctionId;
  private CountDownLatch callbacksLatch;

  public SensorBinaryCheckOut(int nodeId, String device) {
    this.addresseeId = new NodeId((byte) nodeId);

    ApplicationCommandInterceptor commandInterceptor = ApplicationCommandInterceptor.builder()
        .supportedCommandDispatcher(new SupportedCommandDispatcher()
            .registerHandler(AssociationCommandType.ASSOCIATION_REPORT, this::handleAssociationReport)
            .registerHandler(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT, this::handleAssociationGroupingsReport)
            .registerHandler(ConfigurationCommandType.CONFIGURATION_REPORT, this::handleConfigurationReport))
        .build();

    InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
        .addInterceptor(commandInterceptor)
        .addInterceptor(this::handleSendDataCallback);

    this.controller = GeneralAsynchronousController.builder()
        .callbackHandler(callbacksHandler)
        .device(device)
        .build();
  }

  private void handleAssociationReport(ZWaveSupportedCommand command) {
    AssociationReport report = (AssociationReport) command;
    StringBuffer logMessage = new StringBuffer("\n")
        .append(String.format("  association group: %s\n", report.getGroupId()))
        .append(String.format("  max nodes supported: %s\n", report.getMaxNodesCountSupported()))
        .append(String.format("  present nodes count: %s\n", report.getNodesCount()))
        .append(String.format("  present nodes: %s\n", Arrays.stream(report.getNodeIds())
          .map(nodeId -> String.format("%02X", nodeId.getId()))
            .collect(Collectors.joining(","))));
    System.out.printf("%\n", logMessage.toString());
    callbacksLatch.countDown();
  }

  private void handleAssociationGroupingsReport(ZWaveSupportedCommand command) {
    AssociationGroupingsReport report = (AssociationGroupingsReport) command;
    System.out.printf("supported association groups count %s\n", report.getGroupsCount());
    callbacksLatch.countDown();
  }

  private void handleConfigurationReport(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    System.out.printf("parameter %s value %s\n", report.getParameterNumber(), report.getValue());
    callbacksLatch.countDown();
  }

  private void handleSendDataCallback(ZWaveCallback callback) {
    if (callback.getSerialCommand() == SerialCommand.SEND_DATA) {
      SendDataCallback sendDataCallback = (SendDataCallback) callback;
      if (sendDataCallback.getFunctionCallId() == callbackFunctionId) {
        System.out.printf("Send Data Callback received with status: %s\n", sendDataCallback.getTransmitCompletionStatus());
        callbacksLatch.countDown();
      } else {
        log.debug("Received Send Data Callback but of not correlated function id {}", sendDataCallback.getFunctionCallId());
      }
    } else {
      log.debug("Skipped frame {}", callback.getSerialCommand());
    }
  }

  private byte nextFuncId() {
    if (++callbackFunctionId == 0) {
      callbackFunctionId++;
    }
    return callbackFunctionId;
  }

  private void send(String message, SerialRequest request) throws Exception {
    Thread.sleep(500);
    callbacksLatch = new CountDownLatch(2);
    SendDataResponse response = controller.requestResponseFlow(request);
    System.out.printf("%s. Response status: %s\n", message, response.isRequestAccepted());
    if (callbacksLatch.await(5, TimeUnit.SECONDS)) {
      log.debug("Transaction successful");
    } else {
      System.out.printf("Transaction timed out\n");
    }
  }

  private void learnAssociations() throws Exception {
    AssociationCommandBuilder commandBuilder = new AssociationCommandBuilder();
    send("Get supported groupings", createSendDataRequest(addresseeId, commandBuilder.buildGetSupportedGroupingsCommand(), nextFuncId()));
    send("Get group 1", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(1), nextFuncId()));
    send("Get group 2", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(2), nextFuncId()));
    send("Get group 3", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(3), nextFuncId()));
  }

  private void learnConfiguration() throws Exception {
    log.debug("Checking configuration");
    ConfigurationCommandBuilder commandBuilder = new ConfigurationCommandBuilder();
    for (int paramNumber = 1; paramNumber <= 14; paramNumber++) {
      send("Send get parameter " + paramNumber, createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(paramNumber), nextFuncId()));
    }
  }

  @Override
  public void close() throws SerialPortException {
    controller.close();
  }

  public static void main(String[] args) throws Exception {
    try (
      SensorBinaryCheckOut checkout = new SensorBinaryCheckOut(3, System.getProperty("zwave.device", DEFAULT_DEVICE));
    ) {
      checkout.learnAssociations();
      checkout.learnConfiguration();
    }
  }
}
