package com.rposcro.jwavez.samples;

import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.FlowException;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.GetCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetControllerCapabilitiesRequest;
import com.rposcro.jwavez.serial.frames.requests.GetInitDataRequest;
import com.rposcro.jwavez.serial.frames.requests.GetSUCNodeIdRequest;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetControllerCapabilitiesResponse;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.GetSUCNodeIdResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class DongleCheckOut extends AbstractExample {

  private void checkDongleIds(BasicSynchronousController controller) throws FlowException {
    SerialRequest request = MemoryGetIdRequest.createMemoryGetIdRequest();
    MemoryGetIdResponse response = controller.requestResponseFlow(request);

    System.out.printf("Home Id: %02x\n", response.getHomeId());
    System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
  }

  private void checkNodesIds(BasicSynchronousController controller) throws FlowException {
    SerialRequest request = GetInitDataRequest.createGetInitDataRequest();
    GetInitDataResponse response = controller.requestResponseFlow(request);

    System.out.print("Included nodes: ");
    response.getNodes().stream().forEach(node -> {
      System.out.printf("%02x ", node.getId());
    });
    System.out.println();
    System.out.println();
  }

  private void checkSUCId(BasicSynchronousController controller) throws FlowException {
    SerialRequest request = GetSUCNodeIdRequest.createGetSUCNodeIdRequest();
    GetSUCNodeIdResponse response = controller.requestResponseFlow(request);

    System.out.printf("SUC node Id: %02x\n", response.getSucNodeId().getId());
    System.out.println();
  }

  private void controllerCapabilities(BasicSynchronousController controller) throws FlowException {
    SerialRequest request = GetControllerCapabilitiesRequest.createGetControllerCapabiltiesRequest();
    GetControllerCapabilitiesResponse response = controller.requestResponseFlow(request);

    System.out.printf("Is real primary: %s\n", response.isRealPrimary());
    System.out.printf("Is secondary: %s\n", response.isSecondary());
    System.out.printf("Is SUC: %s\n", response.isSUC());
    System.out.printf("Is SIS: %s\n", response.isSIS());
    System.out.printf("Is on another network: %s\n", response.isOnOtherNetwork());
    System.out.println();
  }

  private void capabilties(BasicSynchronousController controller) throws FlowException {
    SerialRequest request = GetCapabilitiesRequest.createGetCapabilitiesRequest();
    GetCapabilitiesResponse response = controller.requestResponseFlow(request);

    System.out.printf("Manufacturer Id: %02x\n", response.getManufacturerId());
    System.out.printf("Manufacturer product Id: %02x\n", response.getManufacturerProductId());
    System.out.printf("Manufacturer product type: %02x\n", response.getManufacturerProductType());
    System.out.printf("Serial application revision: %02x\n", response.getSerialAppRevision());
    System.out.printf("Serial commands: ");
    response.getSerialCommands().stream().forEach(code -> {
      System.out.printf("%02x ", code);
    });
    System.out.println();
  }

  private void runExample(String device) throws SerialException {
    try (BasicSynchronousController controller = BasicSynchronousController.builder()
        .device(device)
        .build()
        .connect();) {
      checkDongleIds(controller);
      checkNodesIds(controller);
      checkSUCId(controller);
      controllerCapabilities(controller);
      capabilties(controller);
    }
  }

  public static void main(String... args) throws SerialException {
    new DongleCheckOut().runExample(System.getProperty("zwave.device", DEFAULT_DEVICE));
  }
}
