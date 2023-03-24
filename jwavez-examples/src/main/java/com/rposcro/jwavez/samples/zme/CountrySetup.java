package com.rposcro.jwavez.samples.zme;

import com.rposcro.jwavez.samples.AbstractExample;
import com.rposcro.jwavez.serial.buffers.DisposableFrameBuffer;
import com.rposcro.jwavez.serial.controllers.BasicSynchronousController;
import com.rposcro.jwavez.serial.enums.SerialCommand;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.SerialFrameConstants;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CountrySetup extends AbstractExample implements AutoCloseable {

    private final BasicSynchronousController controller;

    public CountrySetup(String device) throws SerialPortException {
        this.controller = BasicSynchronousController.builder()
                .dongleDevice(device)
                .build()
                .connect();
    }

    public void close() throws SerialException {
        controller.close();
    }

    private void setCountryToEU() throws Exception {
        sendWithoutResponse("Change frequency to EU",
            SerialRequest.builder()
                .responseExpected(false)
                .serialCommand(SerialCommand.ZSTICK_SET_CONFIG)
                .frameData(new DisposableFrameBuffer(6)
                        .put(SerialFrameConstants.CATEGORY_SOF)
                        .put((byte) (4))
                        .put(SerialFrameConstants.TYPE_REQ)
                        .put(SerialCommand.ZSTICK_SET_CONFIG.getCode())
                        .putData((byte) 0x00)
                        .putCRC()
                ).build());
    }

    private void sendWithoutResponse(String message, SerialRequest request) throws Exception {
        Thread.sleep(500);
        System.out.printf("\n%s\n", message);
        controller.requestResponseFlow(request);
        System.out.printf("Request sent\n");
    }

    public static void main(String[] args) throws Exception {
        try (
                CountrySetup setup = new CountrySetup("/dev/tty.usbmodem14211");
                //CountrySetup setup = new CountrySetup(System.getProperty("zwave.dongleDevice", DEFAULT_DEVICE));
        ) {
            setup.setCountryToEU();
        }
    }
}
