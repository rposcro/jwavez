package com.rposcro.jwavez.samples.fibaro;

import static com.rposcro.jwavez.serial.frames.requests.SendDataRequest.createSendDataRequest;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.handlers.SupportedCommandDispatcher;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.interceptors.ApplicationCommandInterceptor;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
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

  public SensorBinarySetUp(int nodeId, String device) {
    this.commandBuilder = new ConfigurationCommandBuilder();
    this.addresseeId = new NodeId((byte) nodeId);

    ApplicationCommandInterceptor commandInterceptor = ApplicationCommandInterceptor.builder()
        .supportedCommandDispatcher(new SupportedCommandDispatcher()
            .registerHandler(ConfigurationCommandType.CONFIGURATION_REPORT, this::handleConfigurationReport))
        .build();

    InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
        .addInterceptor(commandInterceptor)
        ;

    this.controller = GeneralAsynchronousController.builder()
        .callbackHandler(callbacksHandler)
        .device(device)
        .build();
  }

  public void close() throws SerialException {
    controller.close();
  }

  private void handleConfigurationReport(ZWaveSupportedCommand command) {
    ConfigurationReport report = (ConfigurationReport) command;
    System.out.printf("parameter %s value %s\n", report.getParameterNumber(), report.getValue());
  }

  private void setMonostableModeForBothInputs() throws Exception {
    send("Set input 1 as monostable",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_MONOSTABLE), nextFlowId()));
    send("Check input 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1), nextFlowId()));
    send("Set input 2 as monostable",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_MONOSTABLE), nextFlowId()));
    send("Check input 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2), nextFlowId()));
  }

  private void setNOModeForBothInputs() throws Exception {
    send("Set input 1 as NO",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_NO), nextFlowId()));
    send("Check input 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1), nextFlowId()));
    send("Set input 2 as NO",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_NO), nextFlowId()));
    send("Check input 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2), nextFlowId()));
  }

  private void setFrameGenericForBothInputs() throws Exception {
    send("Set input 1 as generic alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_GENERIC), nextFlowId()));
    send("Check alarm 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1), nextFlowId()));
    send("Set input 2 as generic alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_GENERIC), nextFlowId()));
    send("Check alarm 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2), nextFlowId()));
  }

  private void setFrameBasicSetForBothInputs() throws Exception {
    send("Set input 1 as basic set alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_BASIC_SET), nextFlowId()));
    send("Check alarm 1 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1), nextFlowId()));
    send("Set input 2 as basic set alarm",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_BASIC_SET), nextFlowId()));
    send("Check alarm 2 type",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2), nextFlowId()));
  }

  private void enableSceneActivationCommand() throws Exception {
    send("Enable scene activation",
        createSendDataRequest(addresseeId, commandBuilder.buildSetParameterCommand(PARAM_NUM_SCENE_ACTIVATION, SCENE_ACTIVATION_ENABLED), nextFlowId()));
    send("Check scene activation",
        createSendDataRequest(addresseeId, commandBuilder.buildGetParameterCommand(PARAM_NUM_SCENE_ACTIVATION), nextFlowId()));
  }

  private void associateMainController() throws Exception {
    send("Associate main controller (1)",
        createSendDataRequest(addresseeId, new AssociationCommandBuilder().buildSetCommand(3, 1), nextFlowId()));
  }

  private void send(String message, SerialRequest request) throws Exception {
    Thread.sleep(500);
    SendDataCallback callback = controller.requestCallbackFlow(request);
    System.out.printf("%s. Flow status: %s", callback.getTransmitCompletionStatus());
  }

  private byte nextFlowId() {
    return ++callbackFlowId == 0 ? ++callbackFlowId : callbackFlowId;
  }

  public static void main(String[] args) throws Exception {
    try (
        SensorBinarySetUp setup = new SensorBinarySetUp(3, System.getProperty("zwave.device", DEFAULT_DEVICE));
    ) {
      setup.setMonostableModeForBothInputs();
//    setup.setNOModeForBothInputs();
      setup.setFrameGenericForBothInputs();
      setup.setFrameBasicSetForBothInputs();
      setup.enableSceneActivationCommand();
      setup.associateMainController();
    }
  }
}
