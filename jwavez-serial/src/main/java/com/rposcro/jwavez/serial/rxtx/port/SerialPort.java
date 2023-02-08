package com.rposcro.jwavez.serial.rxtx.port;

import com.rposcro.jwavez.serial.exceptions.SerialPortException;

import java.nio.ByteBuffer;

public interface SerialPort extends AutoCloseable {

    void connect(String device) throws SerialPortException;

    void reconnect() throws SerialPortException;

    void disconnect() throws SerialPortException;

    int readData(ByteBuffer buffer) throws SerialPortException;

    int writeData(ByteBuffer buffer) throws SerialPortException;

    @Override
    default void close() throws SerialPortException {
        this.disconnect();
    }
}
