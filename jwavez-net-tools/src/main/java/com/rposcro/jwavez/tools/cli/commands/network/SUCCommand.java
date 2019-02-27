package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.frames.callbacks.SetSUCNodeIdCallback;
import com.rposcro.jwavez.serial.frames.requests.GetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.requests.SetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.commands.AbstractSyncBasedCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandExecutionException;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.SUCOptions;

public class SUCCommand extends AbstractSyncBasedCommand {

  private SUCOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new SUCOptions(args);
  }

  @Override
  public void execute() throws CommandExecutionException {
    System.out.println("SUC command for " + options.getDevice());
    connect(options);
    try {
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
    } catch(FlowException e) {
      System.out.println("Command interrupted by an error " + e.getMessage());
    }
  }

  private void readSUC() throws FlowException {
    System.out.println("Checking SUC id on this dongle ...");
    GetSUCNodeIdResponse response = controller.requestResponseFlow(GetSUCNodeIdRequest.createGetSUCNodeIdRequest());
    System.out.printf("  SUC Id: 0x%02x\n", response.getSucNodeId().getId());
  }

  private void setThisSUC() throws FlowException {
    System.out.println("Reading this dongle's id...");
    MemoryGetIdResponse response = controller.requestResponseFlow(MemoryGetIdRequest.createMemoryGetIdRequest());
    NodeId thisId = response.getNodeId();
    System.out.printf("This dongle's id is 0x%02x\n", thisId.getId());

    System.out.println("Setting up this dongle as SUC...");
    SetSUCNodeIdCallback callback = controller.requestCallbackFlow(
        SetSUCNodeIdRequest.createSetRemoteSUCNodeRequest(thisId, true, nextFlowId()),
        options.getTimeout());

    if (callback.isSuccessful()) {
      System.out.println("Dongle set as SUC");
    } else {
      System.out.println("Failed to set this dongle as SUC");
    }
  }

  private void setOtherSUC() throws FlowException {
    System.out.printf("Setting SUC Id as 0x%02x ...\n", options.getOtherId());
    SetSUCNodeIdCallback callback = controller.requestCallbackFlow(
        SetSUCNodeIdRequest.createSetLocalSUCNodeRequest(new NodeId(options.getOtherId()), true),
        options.getTimeout());

    if (callback.isSuccessful()) {
      System.out.println("SUC Id set");
    } else {
      System.out.println("Failed to configure SUC Id");
    }
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("suc", "-r", "-d", "/dev/tty.usbmodem1421");
  }
}
