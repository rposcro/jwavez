package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;

import lombok.Builder;

public class FrameOutboundStream {

    private final byte[] ackBuffer;
    private final byte[] nakBuffer;
    private final byte[] canBuffer;

    private SerialPort serialPort;

    @Builder
    public FrameOutboundStream(SerialPort serialPort) {
        this();
        this.serialPort = serialPort;
    }

    private FrameOutboundStream() {
        this.ackBuffer = new byte[] { CATEGORY_ACK };
        this.nakBuffer = new byte[] { CATEGORY_NAK };
        this.canBuffer = new byte[] { CATEGORY_CAN };
    }

    public void writeCAN() throws SerialPortException {
        serialPort.writeData(canBuffer);
    }

    public void writeNAK() throws SerialPortException {
        serialPort.writeData(nakBuffer);
    }

    public void writeACK() throws SerialPortException {
        serialPort.writeData(ackBuffer);
    }

    public void writeSOF(ImmutableBuffer sofBuffer) throws SerialPortException {
        serialPort.writeData(sofBuffer);
    }
}
