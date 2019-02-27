package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.requests.GetControllerCapabilitiesRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.GetSUCNodeIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetControllerCapabilitiesResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetSUCNodeIdResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.probe.transactions.SetLearnModeTransaction;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.jwavez.serial.probe.transactions.TransactionStatus;
import com.rposcro.jwavez.tools.cli.commands.AbstractDeviceTimeoutCommand;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.NetworkLearnOptions;
import java.util.stream.Collectors;

public class NetworkLearnCommand extends AbstractDeviceTimeoutCommand {

  private NetworkLearnOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NetworkLearnOptions(args);
  }

  @Override
  public void execute() {
    try {
      System.out.println("Starting network learn mode on " + options.getDevice() + "...");
      connect(options);
      startLearning();
    } catch(Exception e) {
      System.out.println("Network learning interrupted by an error: " + e.getMessage());
    }
  }

  private void startLearning() throws Exception {
    SetLearnModeTransaction transaction = new SetLearnModeTransaction();
    TransactionResult<NodeId> result = serialChannel.executeTransaction(transaction, options.getTimeout()).get();

    if (result.getStatus() == TransactionStatus.Completed) {
      NodeId nodeId = result.getResult();
      if (nodeId.getId() != 0) {
        System.out.println(String.format("Dongle successfully added into network, assigned id: %02X", nodeId.getId()));
      } else {
        System.out.println("Dongle successfully removed from network");
      }

      if (options.showSummary()) {
        showSummary();
      }
    } else if (result.getStatus() == TransactionStatus.Cancelled) {
      System.out.println("Learning stopped by timeout");
    } else {
      System.out.println("Learning failed by unknown reason");
    }
  }

  private void showSummary() {
    try {
      MemoryGetIdResponseFrame memId = (MemoryGetIdResponseFrame) serialChannel.sendFrameWithResponseAndWait(new MemoryGetIdRequestFrame()).getResult();
      System.out.println(String.format("  HomeId: %02x", memId.getHomeId()));
      System.out.println(String.format("  Dongle NodeId: %02x", memId.getNodeId().getId()));
      GetInitDataResponseFrame iniDt = (GetInitDataResponseFrame) serialChannel.sendFrameWithResponseAndWait(new GetInitDataRequestFrame()).getResult();
      System.out.println(String.format("  Nodes: %s", iniDt.getNodeList().stream().map(NodeId::getId).collect(Collectors.toList())));
      GetControllerCapabilitiesResponseFrame ccap = (GetControllerCapabilitiesResponseFrame) serialChannel.sendFrameWithResponseAndWait(new GetControllerCapabilitiesRequestFrame()).getResult();
      System.out.println(String.format("  Is real primary: %s", ccap.isRealPrimary()));
      System.out.println(String.format("  Is secondary: %s", ccap.isSecondary()));
      System.out.println(String.format("  Is SUC: %s", ccap.isSUC()));
      System.out.println(String.format("  Is SIS: %s", ccap.isSIS()));
      System.out.println(String.format("  Is on another network: %s", ccap.isOnOtherNetwork()));
      GetSUCNodeIdResponseFrame sucId = (GetSUCNodeIdResponseFrame) serialChannel.sendFrameWithResponseAndWait(new GetSUCNodeIdRequestFrame()).getResult();
      System.out.println(String.format("  SUC node id: %02X", sucId.getSucNodeId()));
    } catch(Exception e) {
      System.out.println("Failed to read summary");
    }
  }
}
