package com.rposcro.jwavez.samples;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class MemoryGetIdExample extends AbstractExample {

    private void runExample(String device) throws SerialException {
        try (BasicSynchronousController controller = BasicSynchronousController.builder()
                .dongleDevice(device)
                .build()
                .connect();) {

            SerialRequest request = JwzSerialSupport.defaultSupport().serialRequestFactory().dongleFactsRequestBuilder()
                    .createMemoryGetIdRequest();
            MemoryGetIdResponse response = controller.requestResponseFlow(request);

            System.out.printf("Home Id: %02x\n", response.getHomeId());
            System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
        }
    }

    public static void main(String... args) throws SerialException {
        new MemoryGetIdExample().runExample(System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE));
    }
}
