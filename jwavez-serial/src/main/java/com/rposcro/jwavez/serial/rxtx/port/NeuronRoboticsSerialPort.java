package com.rposcro.jwavez.serial.rxtx.port;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import gnu.io.NRSerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class NeuronRoboticsSerialPort implements SerialPort {

    private static final int DEFAULT_BAUD_RATE = 115200;

    private String device;
    private NRSerialPort port;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Semaphore rxTxSemaphore = new Semaphore(1);

    private ReadableByteChannel inputChannel;

    @Override
    public synchronized void connect(String device) throws SerialPortException {
        try {
            if (this.port != null) {
                throw new SerialPortException("Port is already connected with dongleDevice %s!", device);
            }

            this.device = device;
            this.port = new NRSerialPort(device, DEFAULT_BAUD_RATE);
            this.port.connect();
            this.inputStream = port.getInputStream();
            this.inputChannel = Channels.newChannel(inputStream);
            this.outputStream = port.getOutputStream();
        } catch (Exception e) {
            throw new SerialPortException(e, "Could not connect with dongleDevice %s!", device);
        }
    }

    @Override
    public synchronized void reconnect() throws SerialPortException {
        disconnect();
        connect(device);
    }

    @Override
    public synchronized void disconnect() throws SerialPortException {
        if (port != null) {
            try {
                port.disconnect();
            } catch (Exception e) {
                throw new SerialPortException(e, "Could not successfully disconnect from serial channel!");
            } finally {
                port = null;
            }
        }
    }

    @Override
    public int readData(ByteBuffer buffer) throws SerialPortException {
        try {
            if (inputStream.available() > 0) {
                return inputChannel.read(buffer);
            } else {
                return 0;
            }
        } catch (IOException e) {
            throw new SerialPortException(e, "Exception occurred while reading from channel!");
        }
    }

    public int writeData(byte[] data) throws SerialPortException {
        return writeData(() -> data);
    }

    @Override
    public int writeData(ImmutableBuffer buffer) throws SerialPortException {
        return writeData(() -> buffer.cloneBytes());
    }

    private int writeData(Supplier<byte[]> dataSupplier) throws SerialPortException {
        try {
            rxTxSemaphore.acquire();
            byte[] data = dataSupplier.get();
            outputStream.write(data);
            return data.length;
        } catch (IOException e) {
            throw new SerialPortException(e, "Exception occurred while writing to channel!");
        } catch (InterruptedException e) {
            throw new SerialPortException(e, "Thread interrupted while waiting for transmission!");
        } finally {
            rxTxSemaphore.release();
        }
    }
}
