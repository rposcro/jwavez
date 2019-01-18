package com.rposcro.jwavez.tools.cli.commands;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.transactions.RequestResponseFlowTransaction;
import com.rposcro.jwavez.serial.transactions.SetSUCNodeIdTransaction;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.SUCOptions;

public class SUCCommand extends AbstractDeviceTimeoutCommand {

  private SUCOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new SUCOptions(args);
  }

  @Override
  public void execute() {
    System.out.println("SUC command for " + options.getDevice());
    try {
      connect(options);
      switch (options.getAction()) {
        case READ:
          readSUC();
          break;
        case SET_THIS:
          setThisSUC();
          break;
        case SET_OTHER:
          setOtherSUC();
          break;
      }
    } catch(Exception e) {
      System.out.println("Command interrupted by an error " + e.getMessage());
    }
  }

  private void readSUC() throws Exception {
    System.out.println("Checking SUC id on this dongle ...");
    TransactionResult<GetSUCNodeIdResponseFrame> trResult = serialChannel.executeTransaction(
      new RequestResponseFlowTransaction<GetSUCNodeIdResponseFrame>(
          new GetSUCNodeIdRequestFrame(), GetSUCNodeIdResponseFrame.class)).get();
    if (trResult.getStatus() == TransactionStatus.Completed) {
      System.out.println("  SUC Id: " + String.format("0x%02x ", trResult.getResult().getSucNodeId()));
    } else {
      System.out.println("Failed to get SUC id");
    }
  }

  private void setThisSUC() throws Exception {
    System.out.println("Reading this dongle's id...");
    TransactionResult<MemoryGetIdResponseFrame> idResult = serialChannel.sendFrameWithResponseAndWait(new MemoryGetIdRequestFrame());
    if (idResult.getStatus() != TransactionStatus.Completed) {
      System.out.println("Failed to read this dongle's id");
      return;
    }
    NodeId thisId = idResult.getResult().getNodeId();
    System.out.println(String.format("Thos dongle's id is 0x%02x", thisId.getId()));

    System.out.println("Setting up this dongle as SUC...");
    TransactionResult result = serialChannel.executeTransaction(new SetSUCNodeIdTransaction(thisId, true, true)).get();
    if (result.getStatus() == TransactionStatus.Completed) {
      System.out.println("Dongle set as SUC");
    } else {
      System.out.println("Failed to set this dongle as SUC");
    }
  }

  private void setOtherSUC() throws Exception {
    System.out.println(String.format("Setting SUC Id as 0x%02x ...", options.getOtherId()));
    SetSUCNodeIdTransaction transaction = new SetSUCNodeIdTransaction(new NodeId(options.getOtherId()), true, false);
    TransactionResult<Void> result = serialChannel.executeTransaction(transaction).get();
    if (result.getStatus() == TransactionStatus.Completed) {
      System.out.println("SUC Id set");
    } else {
      System.out.println("Failed to configure SUC Id");
    }
  }
}
