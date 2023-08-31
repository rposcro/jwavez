package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.serial.rxtx.port.SerialPort;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockedSerialPort implements SerialPort {

    private List<List<Integer>> dataChunks;
    private Iterator<List<Integer>> chunksIterator;
    private Iterator<Integer> seriesIterator;

    private List<Integer> outboundData;

    public MockedSerialPort() {
        this.dataChunks = new ArrayList<>();
        this.outboundData = new ArrayList<>(100);
    }

    public MockedSerialPort(List<Integer> inDataSeries) {
        this();
        this.dataChunks.add(inDataSeries);
    }

    public MockedSerialPort reset() {
        this.chunksIterator = dataChunks.iterator();
        return this;
    }

    public MockedSerialPort addSeries(List<Integer> inDataSeries) {
        this.dataChunks.add(inDataSeries);
        return this;
    }

    public boolean inboundDataAvailable() {
        return chunksIterator.hasNext() || seriesIterator.hasNext();
    }

    @Override
    public void connect(String device) {
    }

    @Override
    public void reconnect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public int readData(ByteBuffer buffer) {
        updateStream();
        int readCount = 0;
        while (seriesIterator != null && seriesIterator.hasNext() && buffer.hasRemaining()) {
            buffer.put(seriesIterator.next().byteValue());
            readCount++;
        }
        return readCount;
    }

    @Override
    public int writeData(ImmutableBuffer buffer) {
        int size = buffer.available();
        while (buffer.hasNext()) {
            outboundData.add(buffer.nextByte() & 0xff);
        }
        return size;
    }

    @Override
    public int writeData(byte[] data) {
        for (byte bt: data) {
            outboundData.add(bt & 0xff);

        }
        return data.length;
    }

    private void updateStream() {
        if ((seriesIterator == null || !seriesIterator.hasNext()) && chunksIterator.hasNext()) {
            seriesIterator = chunksIterator.next().iterator();
        }
    }
}
