package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.controllers.SingleRequestController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class MemoryGetIdExample extends AbstractExample {

  private void runExample(String device) throws SerialException {
    try (SingleRequestController controller = SingleRequestController.builder()
        .device(device)
        .build()
        .connect();) {

      SerialRequest request = MemoryGetIdRequest.createSerialRequest();
      MemoryGetIdResponse response = controller.requestResponseFlow(request);

      System.out.printf("Home Id: %02x\n", response.getHomeId());
      System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
    }
  }

  public static void main(String... args) throws SerialException {
    new MemoryGetIdExample().runExample(System.getProperty("zwave.device", DEFAULT_DEVICE));
  }
}
