package com.rposcro.jwavez.serial.rxtz;

import com.rposcro.jwavez.serial.rxtx.SerialConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MockedSerialConnection implements SerialConnection {

  private List<List<Integer>> dataChunks;
  private Iterator<List<Integer>> chunksIterator;
  private Iterator<Integer> seriesIterator;

  private List<Integer> outboundData;

  public MockedSerialConnection() {
    this.dataChunks = new ArrayList<>();
    this.outboundData = new ArrayList<>(100);
  }

  public MockedSerialConnection(List<Integer> inDataSeries) {
    this();
    this.dataChunks.add(inDataSeries);
  }

  public MockedSerialConnection reset() {
    this.chunksIterator = dataChunks.iterator();
    return this;
  }

  public MockedSerialConnection addSeries(List<Integer> inDataSeries) {
    this.dataChunks.add(inDataSeries);
    return this;
  }

  @Override
  public void connect(String device) {}

  @Override
  public int readData(ByteBuffer buffer) {
    updateStream();
    int readCount = 0;
    while(seriesIterator != null && seriesIterator.hasNext() && buffer.hasRemaining()) {
      buffer.put(seriesIterator.next().byteValue());
      readCount++;
    }
    return readCount;
  }

  @Override
  public int writeData(ByteBuffer buffer) {
    int size = buffer.remaining();
    while (buffer.hasRemaining()) {
      outboundData.add(buffer.get() & 0xff);
    }
    return size;
  }

  private void updateStream() {
    if ((seriesIterator == null || !seriesIterator.hasNext()) && chunksIterator.hasNext()) {
      seriesIterator = chunksIterator.next().iterator();
    }
  }
}
