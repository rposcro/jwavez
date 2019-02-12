package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.controllers.SingleRequestController;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.GetInitDataRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class CheckNodesInNetwork extends AbstractExample {

  private void checkDongleIds(SingleRequestController controller) throws FlowException {
    SerialRequest request = MemoryGetIdRequest.createSerialRequest();
    MemoryGetIdResponse response = controller.requestResponseFlow(request);

    System.out.printf("Home Id: %02x\n", response.getHomeId());
    System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
  }

  private void checkNodesIds(SingleRequestController controller) throws FlowException {
    SerialRequest request = GetInitDataRequest.createSerialRequest();
    GetInitDataResponse response = controller.requestResponseFlow(request);

    System.out.print("Included nodes: ");
    response.getNodes().stream().forEach(node -> {
      System.out.printf("%02x ", node.getId());
    });
  }

  private void runExample(String device) throws SerialException {
    try (SingleRequestController controller = SingleRequestController.builder()
        .device(device)
        .build()
        .connect();) {
      checkDongleIds(controller);
      checkNodesIds(controller);
    }
  }

  public static void main(String... args) throws SerialException {
    new CheckNodesInNetwork().runExample(System.getProperty("zwave.device", DEFAULT_DEVICE));
  }
}
