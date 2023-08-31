package com.rposcro.jwavez.samples;

import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.responses.GetInitDataResponse;
import com.rposcro.jwavez.serial.frames.responses.MemoryGetIdResponse;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public class CheckNodesInNetwork extends AbstractExample {

    private void checkDongleIds(BasicSynchronousController controller) throws SerialException {
        SerialRequest request = JwzSerialSupport.defaultSupport().serialRequestFactory().dongleFactsRequestBuilder()
                .createMemoryGetIdRequest();
        MemoryGetIdResponse response = controller.requestResponseFlow(request);

        System.out.printf("Home Id: %02x\n", response.getHomeId());
        System.out.printf("Node Id: %02x\n", response.getNodeId().getId());
    }

    private void checkNodesIds(BasicSynchronousController controller) throws SerialException {
        SerialRequest request = JwzSerialSupport.defaultSupport().serialRequestFactory().dongleFactsRequestBuilder()
                .createGetInitDataRequest();
        GetInitDataResponse response = controller.requestResponseFlow(request);

        System.out.print("Included nodes: ");
        response.getNodes().stream().forEach(node -> {
            System.out.printf("%02x ", node.getId());
        });
    }

    private void runExample(String device) throws SerialException {
        try (BasicSynchronousController controller = BasicSynchronousController.builder()
                .dongleDevice(device)
                .build()
                .connect();) {
            checkDongleIds(controller);
            checkNodesIds(controller);
        }
    }

    public static void main(String... args) throws SerialException {
        new CheckNodesInNetwork().runExample(System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE));
    }
}
