package com.rposcro.jwavez.samples.fibaro;

import static com.rposcro.jwavez.serial.frames.requests.SendDataRequest.createSendDataRequest;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.buffers.ViewBuffer;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.frames.responses.SendDataResponse;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinarySetUp extends AbstractExample implements AutoCloseable {

  private static final int PARAM_NUM_IN_TYPE_1 = 3;
  private static final int PARAM_NUM_IN_TYPE_2 = 4;
  private static final int PARAM_NUM_CTRL_FRM_1 = 5;
  private static final int PARAM_NUM_CTRL_FRM_2 = 6;
  private static final int PARAM_NUM_SCENE_ACTIVATION = 14;

  private static final byte INPUT_TYPE_MONOSTABLE = (byte) 2;
  private static final byte INPUT_TYPE_NO = (byte) 0;
  private static final byte SCENE_ACTIVATION_ENABLED = (byte) 1;
  private static final byte CTRL_FRM_GENERIC = (byte) 0;
  private static final byte CTRL_FRM_BASIC_SET = (byte) 255;

  private byte callbackFlowId;
  private final NodeId addresseeId;
  private final GeneralAsynchronousController controller;
  private final ConfigurationCommandBuilder commandBuilder;

  public SensorBinarySetUp(int nodeId, String device) throws SerialPortException {
    this.commandBuilder = new ConfigurationCommandBuilder();
    this.addresseeId = new NodeId((byte) nodeId);
    this.callbackFlowId = (byte) 0x0e;

    InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
        .addViewBufferInterceptor(this::interceptViewBuffer)
        .addCallbackInterceptor(new ApplicationCommandInterceptor()
            .registerCommandHandler(ConfigurationCommandType.CONFIGURATION_REPORT, this::handleConfigurationReport))
        ;

    this.controller = GeneralAsynchronousController.builder()
        .callbackHandler(callbacksHandler)
        .dongleDevice(device)
        .build()
        .connect();
  }

  public void close() throws SerialException {
    controller.close();
  }

  private void handleConfigurationReport(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    System.out.printf("  parameter %s value %s\n", report.getParameterNumber(), report.getValue());
  }

  private void interceptViewBuffer(ViewBuffer buffer) {
    log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
  }

  private void setMonostableModeForBothInputs() throws Exception {
    sendWithResponse("Set input 1 as monostable",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_MONOSTABLE), nextFlowId()));
    sendWithCallback("Check input 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1), nextFlowId()));
    sendWithResponse("Set input 2 as monostable",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_MONOSTABLE), nextFlowId()));
    sendWithCallback("Check input 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2), nextFlowId()));
  }

  private void setNOModeForBothInputs() throws Exception {
    sendWithResponse("Set input 1 as NO",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_NO), nextFlowId()));
    sendWithCallback("Check input 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1), nextFlowId()));
    sendWithResponse("Set input 2 as NO",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_NO), nextFlowId()));
    sendWithCallback("Check input 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2), nextFlowId()));
  }

  private void setFrameGenericForBothInputs() throws Exception {
    sendWithResponse("Set input 1 as generic alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_GENERIC), nextFlowId()));
    sendWithCallback("Check alarm 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1), nextFlowId()));
    sendWithResponse("Set input 2 as generic alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_GENERIC), nextFlowId()));
    sendWithCallback("Check alarm 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2), nextFlowId()));
  }

  private void setFrameBasicSetForBothInputs() throws Exception {
    sendWithResponse("Set input 1 as basic set alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_BASIC_SET), nextFlowId()));
    sendWithCallback("Check alarm 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1), nextFlowId()));
    sendWithResponse("Set input 2 as basic set alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_BASIC_SET), nextFlowId()));
    sendWithCallback("Check alarm 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2), nextFlowId()));
  }

  private void enableSceneActivationCommand() throws Exception {
    sendWithResponse("Enable scene activation",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_SCENE_ACTIVATION, SCENE_ACTIVATION_ENABLED), nextFlowId()));
    sendWithCallback("Check scene activation",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_SCENE_ACTIVATION), nextFlowId()));
  }

  private void associateMainController() throws Exception {
    sendWithResponse("Associate main controller (1)",
        createSendDataRequest(addresseeId, new AssociationCommandBuilder().buildSetCommand(3, 1), nextFlowId()));
  }

  private void sendWithCallback(String message, SerialRequest request) throws Exception {
    Thread.sleep(500);
    System.out.printf("\n%s\n", message);
    SendDataCallback callback = controller.requestCallbackFlow(request);
    System.out.printf("Flow status: %s\n", callback.getTransmitCompletionStatus());
  }

  private void sendWithResponse(String message, SerialRequest request) throws Exception {
    Thread.sleep(500);
    System.out.printf("\n%s\n", message);
    SendDataResponse response = controller.requestResponseFlow(request);
    System.out.printf("Response status: %s\n", response.isRequestAccepted());
  }

  private byte nextFlowId() {
    return ++callbackFlowId == 0 ? ++callbackFlowId : callbackFlowId;
  }

  public static void main(String[] args) throws Exception {
    try (
        SensorBinarySetUp setup = new SensorBinarySetUp(3, System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE));
    ) {
      setup.setMonostableModeForBothInputs();
      //setup.setNOModeForBothInputs();
      setup.setFrameBasicSetForBothInputs();
      //setup.setFrameGenericForBothInputs();
      setup.enableSceneActivationCommand();
      setup.associateMainController();
    }
  }
}
