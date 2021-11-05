package com.rposcro.jwavez.tools.cli.commands.network;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.controllers.inclusion.SetLearnModeController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.GetControllerCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetInitDataRequest;
import com.rposcro.jwavez.serial.frames.requests.GetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.tools.cli.commands.Command;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.NetworkLearnOptions;
import com.rposcro.jwavez.tools.utils.ProcedureUtil;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkLearnCommand implements Command {

  private NetworkLearnOptions options;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new NetworkLearnOptions(args);
  }

  @Override
  public void execute() {
    System.out.printf("Starting node learn mode transaction on %s ...\n", options.getDevice());
    ProcedureUtil.executeProcedure(this::executeLearning);
    System.out.println("Network learn protocol finished");

    if (options.showSummary()) {
      System.out.printf("Checking dongle summary for %s ...\n", options.getDevice());
      ProcedureUtil.executeProcedure(this::executeSummary);
    }
  }

  private void executeLearning() throws SerialException {
    try (
        SetLearnModeController controller = SetLearnModeController.builder()
            .dongleDevice(options.getDevice())
            .build()
    ) {
      controller.connect();
      System.out.println("Awaiting for learn protocol ...");
      Optional<NodeId> nodeId = controller.activateLearnMode();
      processLearningResult(nodeId);
    }
  }

  private void processLearningResult(Optional<NodeId> optionalNodeId) {
    if (optionalNodeId.isPresent()) {
      byte nodeId = optionalNodeId.get().getId();
      if (nodeId != 0) {
        System.out.println(String.format("Dongle successfully added into network, assigned id: %02X", nodeId));
      } else {
        System.out.println("Dongle successfully removed from network");
      }
    } else {
      System.out.println("No result information received from completed transaction");
    }
  }

  private void executeSummary() throws SerialException {
    try (
        BasicSynchronousController controller = BasicSynchronousController.builder()
            .dongleDevice(options.getDevice())
            .build()
    ) {
      controller.connect();

      MemoryGetIdResponse memoryGetIdResponse = controller.requestResponseFlow(MemoryGetIdRequest.createMemoryGetIdRequest());
      System.out.println(String.format("  HomeId: %02x", memoryGetIdResponse.getHomeId()));
      System.out.println(String.format("  Dongle NodeId: %02x", memoryGetIdResponse.getNodeId().getId()));

      GetInitDataResponse getInitDataResponse = controller.requestResponseFlow(GetInitDataRequest.createGetInitDataRequest());
      System.out.println(String.format("  Nodes: %s", getInitDataResponse.getNodes().stream().map(NodeId::getId).collect(Collectors.toList())));

      GetControllerCapabilitiesResponse ccap = controller.requestResponseFlow(GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest());
      System.out.println(String.format("  Is real primary: %s", ccap.isRealPrimary()));
      System.out.println(String.format("  Is secondary: %s", ccap.isSecondary()));
      System.out.println(String.format("  Is SUC: %s", ccap.isSUC()));
      System.out.println(String.format("  Is SIS: %s", ccap.isSIS()));
      System.out.println(String.format("  Is on another network: %s", ccap.isOnOtherNetwork()));

      GetSUCNodeIdResponse sucId = controller.requestResponseFlow(GetSUCNodeIdRequest.createGetSUCNodeIdRequest());
      System.out.println(String.format("  SUC node id: %02X", sucId.getSucNodeId()));
    }
  }
}
