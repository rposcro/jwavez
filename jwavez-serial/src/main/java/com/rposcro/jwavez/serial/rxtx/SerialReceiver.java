package com.rposcro.jwavez.serial.rxtx;

import static com.rposcro.jwavez.serial.frame.constants.FrameCategory.*;

import com.rposcro.jwavez.serial.utils.ByteBuffer;
import com.rposcro.jwavez.serial.utils.FrameUtil;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialReceiver {

  private static final long TIMEOUT_NEXT_BYTE = 100;
  private static final long TIMEOUT_NEXT_FRAME = 1500;

  private DataInputStream serialStream;
  private ByteBuffer buffer;

  private final ExecutorService taskExecutor;
  private final Callable<Byte> readByteTask;

  public SerialReceiver(InputStream serialStream) {
    this.serialStream = new DataInputStream(serialStream);
    this.buffer = new ByteBuffer(256);
    this.taskExecutor = Executors.newCachedThreadPool();
    this.readByteTask  = new Callable<Byte>() {
      public Byte call() throws IOException {
        return SerialReceiver.this.serialStream.readByte();
      }
    };
  }

  public boolean dataAvailable() throws IOException {
    return serialStream.available() > 0;
  }

  public ByteBuffer receiveData() throws Exception {
    buffer.clear();
    byte frameCategory = nextFrameStart();
    buffer.put(frameCategory);

    if (SOF.getCode() == frameCategory) {
      int length = ((int) nextFrameByte()) & 0xFF;
      buffer.put((byte) length);
      for (int idx = 2; idx < length + 2; idx++) {
        buffer.put(nextFrameByte());
      }
    }

    log.debug("Received data: {}", FrameUtil.bufferToString(buffer));
    return buffer;
  }

  public void purgeStream() throws Exception {
    log.debug("Purging stream: ");
    StringBuffer purgedBytes = new StringBuffer();

    while (serialStream.available() > 0) {
      byte chunk = serialStream.readByte();
      if (log.isDebugEnabled()) {
        purgedBytes.append(String.format("%02x ", chunk));
      }
    }
    log.debug(purgedBytes.toString());
  }

  private byte nextFrameStart() throws Exception {
    return nextByte(TIMEOUT_NEXT_FRAME);
  }

  private byte nextFrameByte() throws Exception {
    return nextByte(TIMEOUT_NEXT_BYTE);
  }

  private byte nextByte(long timeout) throws Exception {
    Future<Byte> future = taskExecutor.submit(readByteTask);
    try {
      Byte result = future.get(timeout, TimeUnit.MILLISECONDS);
      return result;
    } finally {
      future.cancel(true);
    }
  }
}
