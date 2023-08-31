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

    public int writeCAN() throws SerialPortException {
        return serialPort.writeData(canBuffer);
    }

    public int writeNAK() throws SerialPortException {
        return serialPort.writeData(nakBuffer);
    }

    public int writeACK() throws SerialPortException {
        return serialPort.writeData(ackBuffer);
    }

    public int writeSOF(ImmutableBuffer sofBuffer) throws SerialPortException {
        return serialPort.writeData(sofBuffer);
    }
}
