package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.controllers.AllInOneController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.MemoryGetIdRequest;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;

public class MemoryGetIdExample extends AbstractExample {

  private void runTest(String device) throws SerialException {
    try (AllInOneController controller = AllInOneController.builder()
        .device(device)
        .build();) {
      controller
          .connect();
      MemoryGetIdResponse response = controller.requestResponseFlow(MemoryGetIdRequest.createFrameRequest());
      System.out.printf("Home Id: %02x\n", response.getHomeId());
      System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
    }
  }

  public static void main(String... args) throws SerialException {
    new MemoryGetIdExample().runTest(System.getProperty("zwave.device", DEFAULT_DEVICE));
  }
}
