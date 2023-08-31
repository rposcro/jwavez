package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_ACK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_CAN;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_NAK;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.CATEGORY_SOF;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.FRAME_OFFSET_LENGTH;
import static com.rposcro.jwavez.serial.rxtx.SerialFrameConstants.MAX_Z_WAVE_FRAME_SIZE;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.RxTxException;
import com.rposcro.jwavez.serial.exceptions.StreamTimeoutException;
import com.rposcro.jwavez.serial.exceptions.StreamMalformedException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;

import java.nio.ByteBuffer;

import lombok.Builder;

public class FrameInboundStream {

    private final static ImmutableBuffer EMPTY_FRAME_BUFFER = ImmutableBuffer.empty();

    private SerialPort serialPort;
    private RxTxConfiguration configuration;

    private final ByteBuffer streamBuffer;

    @Builder
    public FrameInboundStream(SerialPort serialPort, RxTxConfiguration configuration) {
        this();
        this.serialPort = serialPort;
        this.configuration = configuration;
    }

    private FrameInboundStream() {
        this.streamBuffer = ByteBuffer.allocate(MAX_Z_WAVE_FRAME_SIZE * 2);
        this.streamBuffer.limit(0);
    }

    public ImmutableBuffer nextFrame() throws RxTxException {
        if (!streamBuffer.hasRemaining()) {
            purgeAndLoadBuffer();
        }

        ImmutableBuffer frameBuffer;

        if (streamBuffer.hasRemaining()) {
            frameBuffer = wrapFrameBuffer();
            progressBuffer(frameBuffer.length());
        } else {
            frameBuffer = EMPTY_FRAME_BUFFER;
        }

        return frameBuffer;
    }

    public void purgeStream() throws SerialPortException {
        do {
            streamBuffer.position(0).limit(streamBuffer.capacity());
        } while (serialPort.readData(streamBuffer) > 0);
        streamBuffer.position(0).limit(0);
    }

    private void purgeAndLoadBuffer() throws SerialPortException {
        streamBuffer.position(0);
        streamBuffer.limit(MAX_Z_WAVE_FRAME_SIZE);
        serialPort.readData(streamBuffer);
        streamBuffer.limit(streamBuffer.position());
        streamBuffer.position(0);
    }

    private void progressBuffer(int progressLength) {
        streamBuffer.position(streamBuffer.position() + progressLength);
    }

    private ImmutableBuffer wrapFrameBuffer() throws RxTxException {
        int position = streamBuffer.position();
        byte category = streamBuffer.get(position);

        if (category == CATEGORY_ACK || category == CATEGORY_CAN || category == CATEGORY_NAK) {
            return ImmutableBuffer.overBuffer(streamBuffer.array(), position, 1);
        } else if (category == CATEGORY_SOF) {
            return wrapSOFBuffer();
        } else {
            throw new StreamMalformedException("Unrecognized frame category %02x", category);
        }
    }

    private ImmutableBuffer wrapSOFBuffer() throws RxTxException {
        int position = streamBuffer.position();
        ensureRemaining(3);
        int length = streamBuffer.get(position + FRAME_OFFSET_LENGTH) + 2;
        ensureRemaining(length);
        return ImmutableBuffer.overBuffer(streamBuffer.array(), position, length);
    }

    private void ensureRemaining(int expectedRemaining) throws RxTxException {
        int remaining = streamBuffer.remaining();
        if (remaining < expectedRemaining) {
            refillBuffer(expectedRemaining - remaining);
        }
    }

    private void refillBuffer(int refillSize) throws RxTxException {
        streamBuffer.mark();
        streamBuffer.position(streamBuffer.limit());
        streamBuffer.limit(streamBuffer.limit() + refillSize);
        int refilled = 0;
        long timeOutPoint = System.currentTimeMillis() + configuration.getFrameCompleteTimeout();
        while (refilled < refillSize) {
            refilled += serialPort.readData(streamBuffer);
            if (timeOutPoint < System.currentTimeMillis()) {
                streamBuffer.limit(streamBuffer.position());
                streamBuffer.reset();
                throw new StreamTimeoutException("Frame complete timeout!");
            }
        }
        streamBuffer.reset();
    }
}
