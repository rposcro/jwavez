package com.rposcro.zstick.fibaro;

import com.rposcro.jwavez.commands.controlled.AssociationControlledCommand;
import com.rposcro.jwavez.commands.controlled.ConfigurationControlledCommand;
import com.rposcro.jwavez.model.NodeId;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.debug.ApplicationCommandHandlerCatcher;
import com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinarySetUp {

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

  private SerialManager manager;
  private SerialChannel channel;
  private NodeId nodeId;

  public SensorBinarySetUp(int nodeId, String device) {
    this.nodeId = new NodeId((byte) nodeId);
    this.manager = new SerialManager(device);
    this.channel = manager.connect();
    this.channel.addInboundFrameInterceptor(new ApplicationUpdateCatcher());
    this.channel.addInboundFrameInterceptor(new ApplicationCommandHandlerCatcher());
  }

  private void printResult(String message, TransactionResult<Void> result) {
    StringBuffer logMessage = new StringBuffer(String.format("%s. Status: %s", message, result.getStatus()));
    System.out.println(logMessage.toString());
  }

  private void send(String message, SendDataTransaction transaction) throws Exception {
    Thread.sleep(500);
    printResult(message, channel.executeTransaction(transaction).get());
  }

  private void setMonostableModeForBothInputs() throws Exception {
    send("Set input 1 as monostable",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_MONOSTABLE)));
    send("Check input 1 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1)));
    send("Set input 2 as monostable",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_MONOSTABLE)));
    send("Check input 2 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2)));
  }

  private void setNOModeForBothInputs() throws Exception {
    send("Set input 1 as NO",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_IN_TYPE_1, INPUT_TYPE_NO)));
    send("Check input 1 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_IN_TYPE_1)));
    send("Set input 2 as NO",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_IN_TYPE_2, INPUT_TYPE_NO)));
    send("Check input 2 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_IN_TYPE_2)));
  }

  private void setFrameGenericForBothInputs() throws Exception {
    send("Set input 1 as generic alarm",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_GENERIC)));
    send("Check alarm 1 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1)));
    send("Set input 2 as generic alarm",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_GENERIC)));
    send("Check alarm 2 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2)));
  }

  private void setFrameBasicSetForBothInputs() throws Exception {
    send("Set input 1 as basic set alarm",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_1, CTRL_FRM_BASIC_SET)));
    send("Check alarm 1 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_1)));
    send("Set input 2 as basic set alarm",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_CTRL_FRM_2, CTRL_FRM_BASIC_SET)));
    send("Check alarm 2 type",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_CTRL_FRM_2)));
  }

  private void enableSceneActivationCommand() throws Exception {
    send("Enable scene activation",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildSetParameterCommand(PARAM_NUM_SCENE_ACTIVATION, SCENE_ACTIVATION_ENABLED)));
    send("Check scene activation",
        new SendDataTransaction(nodeId, ConfigurationControlledCommand.buildGetParameterCommand(PARAM_NUM_SCENE_ACTIVATION)));
  }

  private void associateMainController() throws Exception {
    send("Associate main controller (1)",
        new SendDataTransaction(nodeId, AssociationControlledCommand.buildSetCommand(3, 1)));
  }

  public static void main(String[] args) throws Exception {
    SensorBinarySetUp setup = new SensorBinarySetUp(4, "/dev/cu.usbmodem1411");
    //setup.setMonostableModeForBothInputs();
    //setup.setNOModeForBothInputs();
    //setup.setFrameGenericForBothInputs();
    //setup.setFrameBasicSetForBothInputs();
    //setup.enableSceneActivationCommand();
    setup.associateMainController();

    Thread.sleep(60_000);
    System.exit(0);
  }
}
