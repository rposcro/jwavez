package com.rposcro.zwave.samples.probe;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.requests.EnableSUCRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.SetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.EnableSUCResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.SetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.probe.transactions.SetSUCNodeIdTransaction;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;

/**
 * Checks SUC mode on controller stick.
 */
public class SUCTest extends AbstractExample {

  private CheckOutController checkOutService;

  public SUCTest() {
    super("/dev/cu.usbmodem1421");
    checkOutService = new CheckOutController(manager);
  }

  private void checkSUCNode() throws Exception {
    TransactionResult<GetSUCNodeIdResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetSUCNodeIdRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("SUC Node Id: %s", result.getResult().getSucNodeId()));
  }

  private void enableSUCMode() throws Exception {
    TransactionResult<EnableSUCResponseFrame> result = channel.sendFrameWithResponseAndWait(new EnableSUCRequestFrame());
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
  }

  private void setOtherNodeSUC(NodeId nodeId) throws Exception {
    TransactionResult<SetSUCNodeIdResponseFrame> result = channel.sendFrameWithResponseAndWait(
        new SetSUCNodeIdRequestFrame(nodeId, true, (byte) 0xee));
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    System.out.println(String.format("ZWaveResponse: %s", result.getResult().isRequestAccepted()));
  }

  /**
    Attempts to set local controller as the SUC/SIS controller, no callbacks expected in this case
   */
  private void setLocalNodeSUC(NodeId nodeId) throws Exception {
    TransactionResult<Void> result = channel.executeTransaction(
        new SetSUCNodeIdTransaction(nodeId, true, true)).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    checkSUCNode();
    checkOutService.learnControllerCapabilities();
  }

  /**
    Attempts to remove SUC/SIS from local controller, no callbacks expected in this case
   */
  private void removeSUCFromLocalNode(NodeId nodeId) throws Exception {
    TransactionResult<Void> result = channel.executeTransaction(
        new SetSUCNodeIdTransaction(nodeId, false, true)).get();
    System.out.println(String.format("Transaction status: %s", result.getStatus()));
    checkSUCNode();
    checkOutService.learnControllerCapabilities();
  }

  public static void main(String[] args) throws Exception {
    SUCTest test = new SUCTest();
    //test.checkSUCNode();
    //test.enableSUCMode();
    test.setLocalNodeSUC(new NodeId(1));
    //test.removeSUCFromLocalNode(new NodeId(1));

    System.exit(0);
  }
}
