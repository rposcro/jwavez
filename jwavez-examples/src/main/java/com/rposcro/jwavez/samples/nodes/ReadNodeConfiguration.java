package com.rposcro.jwavez.samples.nodes;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.controlled.builders.configuration.ConfigurationCommandBuilder;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.model.TransmitCompletionStatus;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadNodeConfiguration extends AbstractExample {

    private final NodeId addresseeId;
    private final GeneralAsynchronousController controller;

    public ReadNodeConfiguration(int nodeId) throws SerialPortException {
        this.addresseeId = new NodeId((byte) nodeId);
        String device = determineDevice();

        log.debug("Running for device " + device + " and node " + addresseeId.getId());

        InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
                .addFrameBufferInterceptor(this::interceptFrameBuffer);

        this.controller = GeneralAsynchronousController.builder()
                .callbackHandler(callbacksHandler)
                .dongleDevice(determineDevice())
                .build()
                .connect();
    }

    private void interceptFrameBuffer(ImmutableBuffer buffer) {
        log.debug("Callback frame received: {}", BuffersUtil.asString(buffer));
    }

    public void readConfig(int... parameters) {
        try {
            for (int parameter : parameters) {
                log.debug("Sending configuration read request for parameter " + parameter);
                SerialRequest request = JwzSerialSupport.defaultSupport().serialRequestFactory().networkTransportRequestBuilder()
                        .createSendDataRequest(
                                addresseeId,
                                new ConfigurationCommandBuilder().v1().buildGetParameterCommand(parameter),
                                nextFlowId());
                SendDataCallback callback = controller.requestCallbackFlow(request);
                if (callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_OK) {
                    log.debug("Request delivered successfully");
                } else if (callback.getTransmitCompletionStatus() == TransmitCompletionStatus.TRANSMIT_COMPLETE_NO_ACK) {
                    log.debug("Request theoretically delivered however no ack");
                } else {
                    log.debug("Dongle failed to deliver data: " + callback.getTransmitCompletionStatus());
                    return;
                }
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws SerialPortException {
        controller.close();
    }

    public static void main(String[] args) throws Exception {
        ReadNodeConfiguration readConfigApp = new ReadNodeConfiguration(2);
        readConfigApp.readConfig(20);
        readConfigApp.close();
//    System.out.println(System.getenv("JWAVEZ_DEVICE"));
    }
}
