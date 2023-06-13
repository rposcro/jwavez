package com.rposcro.jwavez.samples.fibaro;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.handlers.InterceptableCallbackHandler;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.rposcro.jwavez.serial.utils.BufferUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetRGBWColor extends AbstractExample implements AutoCloseable {

    private final NodeId addresseeId;
    private final GeneralAsynchronousController controller;
    private byte callbackFlowId;

    public SetRGBWColor(int nodeId) throws SerialPortException {
        String device = determineDevice();
        this.callbackFlowId = (byte) 0x0e;
        this.addresseeId = new NodeId((byte) nodeId);

        InterceptableCallbackHandler callbacksHandler = new InterceptableCallbackHandler()
                .addFrameBufferInterceptor(this::interceptFrameBuffer);

        this.controller = GeneralAsynchronousController.builder()
                .callbackHandler(callbacksHandler)
                .dongleDevice(device)
                .build()
                .connect();
    }

    private void interceptFrameBuffer(ImmutableBuffer buffer) {
        log.debug("Callback frame received: {}", BufferUtil.bufferToString(buffer));
    }

    public void switchColor(int red, int green, int blue, int white) {
        try {
            System.out.println("Sending color switch request");
            ZWaveControlledCommand command = new SwitchColorCommandBuilder().v1().buildSetWarmRGBWCommand(
                    (byte) red, (byte) green, (byte) blue, (byte) white, (byte) 1);
            SerialRequest request = JwzSerialSupport.defaultSupport().serialRequestFactory().networkTransportRequestBuilder()
                    .createSendDataRequest(addresseeId, command, callbackFlowId);
            controller.requestCallbackFlow(request);
            System.out.println("Theoretically sent");
        } catch (SerialException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SerialPortException {
        controller.close();
    }

    public static void main(String[] args) throws Exception {
        try (
                SetRGBWColor setColorApp = new SetRGBWColor(7);
        ) {
            setColorApp.switchColor(0, 0, 0, 0);
//      setColorApp.switchColor(0, 0, 0, 255);
//      Thread.sleep(1000);
//      setColorApp.switchColor(0, 255, 0, 0);
//      Thread.sleep(1000);
//      setColorApp.switchColor(0, 0, 255, 0);
//      Thread.sleep(1000);
//      setColorApp.switchColor(0, 0, 0, 255);
//      Thread.sleep(1000);
//      setColorApp.switchColor(0, 0, 0, 255);
        }
    }
}
