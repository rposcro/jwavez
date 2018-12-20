package com.rposcro.zwave.samples.fibaro;

import com.rposcro.jwavez.core.commands.controlled.AssociationCommandBuilder;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.debug.ApplicationCommandHandlerCatcher;
import com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.transactions.SendDataTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensorBinaryCheckOut {

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

  public SensorBinaryCheckOut(int nodeId, String device) {
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

  private void learnAssociations() throws Exception {
    send("Get supported groupings", new SendDataTransaction(nodeId, AssociationCommandBuilder.buildGetSupportedGroupingsCommand()));
    send("Get group 1", new SendDataTransaction(nodeId, AssociationCommandBuilder.buildGetCommand(1)));
    send("Get group 2", new SendDataTransaction(nodeId, AssociationCommandBuilder.buildGetCommand(2)));
    send("Get group 3", new SendDataTransaction(nodeId, AssociationCommandBuilder.buildGetCommand(3)));
  }

  public static void main(String[] args) throws Exception {
    SensorBinaryCheckOut setup = new SensorBinaryCheckOut(4, "/dev/cu.usbmodem1411");
    setup.learnAssociations();

    Thread.sleep(60_000);
    System.exit(0);
  }
}
