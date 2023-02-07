package com.rposcro.jwavez.samples.fibaro;

import static com.rposcro.jwavez.serial.frames.requests.SendDataRequest.createSendDataRequest;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilderV1;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilderV1;
import com.rposcro.jwavez.core.commands.types.AssociationCommandType;
import com.rposcro.jwavez.core.commands.types.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.callbacks.ZWaveCallback;
import com.rposcro.jwavez.serial.frames.responses.SendDataResponse;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinaryCheckOut extends AbstractExample implements AutoCloseable {

  private final NodeId addresseeId;
  private final GeneralAsynchronousController controller;
  private byte callbackFlowId;
  private CountDownLatch callbacksLatch;

  public SensorBinaryCheckOut(int nodeId, String device) throws SerialPortException {
    this.callbackFlowId = nextFlowId();
    this.addresseeId = new NodeId((byte) nodeId);

    ApplicationCommandInterceptor commandInterceptor = new ApplicationCommandInterceptor()
        .registerCommandHandler(AssociationCommandType.ASSOCIATION_REPORT, this::handleAssociationReport)
        .registerCommandHandler(AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT, this::handleAssociationGroupingsReport)
        .registerCommandHandler(ConfigurationCommandType.CONFIGURATION_REPORT, this::handleConfigurationReport);

    InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
        .addCallbackInterceptor(commandInterceptor)
        .addCallbackInterceptor(this::interceptSendDataCallback)
        .addViewBufferInterceptor(this::interceptViewBuffer);

    this.controller = GeneralAsynchronousController.builder()
        .callbackHandler(callbacksHandler)
        .dongleDevice(device)
        .build()
        .connect();
  }

  private void handleAssociationReport(ZWaveSupportedCommand command) {
    AssociationReport report = (AssociationReport) command;
    StringBuffer logMessage = new StringBuffer()
        .append(String.format("  association group: %s\n", report.getGroupId()))
        .append(String.format("  max nodes supported: %s\n", report.getMaxNodesCountSupported()))
        .append(String.format("  present nodes count: %s\n", report.getNodesCount()))
        .append(String.format("  present nodes: %s\n", Arrays.stream(report.getNodeIds())
          .map(nodeId -> String.format("%02X", nodeId.getId()))
            .collect(Collectors.joining(","))));
    System.out.printf("%s\n", logMessage.toString());
    callbacksLatch.countDown();
  }

  private void handleAssociationGroupingsReport(ZWaveSupportedCommand command) {
    AssociationGroupingsReport report = (AssociationGroupingsReport) command;
    System.out.printf("  supported association groups count %s\n\n", report.getGroupsCount());
    callbacksLatch.countDown();
  }

  private void handleConfigurationReport(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    System.out.printf("  parameter %s value %s\n\n", report.getParameterNumber(), report.getValue());
    callbacksLatch.countDown();
  }

  private void interceptViewBuffer(ViewBuffer buffer) {
    log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
  }

  private void interceptSendDataCallback(ZWaveCallback callback) {
    if (callback.getSerialCommand() == SerialCommand.SEND_DATA) {
      SendDataCallback sendDataCallback = (SendDataCallback) callback;
      if (sendDataCallback.getCallbackFlowId() == callbackFlowId) {
        System.out.printf("Send Data Callback received with status: %s\n", sendDataCallback.getTransmitCompletionStatus());
        callbacksLatch.countDown();
      } else {
        log.debug("Received Send Data Callback but of not correlated function id {}", sendDataCallback.getCallbackFlowId());
      }
    } else {
      log.debug("Skipped frame {}", callback.getSerialCommand());
    }
  }

  private void send(String message, SerialRequest request) throws Exception {
    Thread.sleep(500);
    callbacksLatch = new CountDownLatch(2);
    SendDataResponse response = controller.requestResponseFlow(request);
    System.out.printf("%s. Response status: %s\n", message, response.isRequestAccepted());
    if (callbacksLatch.await(5, TimeUnit.SECONDS)) {
      log.debug("Send data flow successful");
    } else {
      System.out.printf("Send data flow timed out\n");
    }
  }

  private void learnAssociations() throws Exception {
    AssociationCommandBuilderV1 commandBuilder = ZWaveControlledCommandBuilder.associationCommandBuilder().v1();
    send("Get supported groupings", createSendDataRequest(addresseeId, commandBuilder.buildGetSupportedGroupingsCommand(), nextFlowId()));
    send("Get group 1", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(1), nextFlowId()));
    send("Get group 2", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(2), nextFlowId()));
    send("Get group 3", createSendDataRequest(addresseeId, commandBuilder.buildGetCommand(3), nextFlowId()));
  }

  private void learnConfiguration() throws Exception {
    log.debug("Checking rxTxConfiguration");
    ConfigurationCommandBuilderV1 commandBuilder = ZWaveControlledCommandBuilder.configurationCommandBuilder().v1();
    for (int paramNumber = 1; paramNumber <= 14; paramNumber++) {
      send("Send get parameter " + paramNumber, createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(paramNumber), nextFlowId()));
    }
  }

  @Override
  public void close() throws SerialPortException {
    controller.close();
  }

  public static void main(String[] args) throws Exception {
    try (
      SensorBinaryCheckOut checkout = new SensorBinaryCheckOut(3, System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE));
    ) {
      checkout.learnAssociations();
      checkout.learnConfiguration();
    }
  }
}
