package com.rposcro.jwavez.samples;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.utils.BuffersUtil;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkListener extends AbstractExample implements AutoCloseable {

    private final GeneralAsynchronousController controller;

    public NetworkListener(String device) throws SerialPortException {
        this.controller = GeneralAsynchronousController.builder()
                .callbackHandler(this::handleFrameBuffer)
                .dongleDevice(device)
                .build()
                .connect();
    }

    public void close() throws SerialException {
        controller.close();
    }

    private void handleFrameBuffer(ImmutableBuffer buffer) {
        System.out.printf("Frame received: %s\n", BuffersUtil.asString(buffer));
    }

    public static void main(String[] args) throws Exception {
        String device = System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE);

        try (
                NetworkListener setup = new NetworkListener(device);
        ) {
            System.out.printf("\nListening to the network via %s\nStop by clicking Ctrl+C\n", device);
            while (true) ;
        }
    }
}
