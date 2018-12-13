package com.rposcro.jwavez.serial.rxtx;

import com.rposcro.jwavez.serial.utils.FrameUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialTransmitter {

  private DataOutputStream serialStream;

  public SerialTransmitter(OutputStream serialStream) {
    this.serialStream = new DataOutputStream(serialStream);
  }

  public void transmitData(byte[] buffer) throws IOException {
    log.debug("Transmitting data: {}", FrameUtil.bufferToString(buffer));
    serialStream.write(buffer);
  }

}
