package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.SerialChannel;
import com.rposcro.jwavez.serial.SerialManager;
import com.rposcro.jwavez.serial.debug.ApplicationCommandHandlerCatcher;
import com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepListening {

  public static void main(String[] args) throws Exception {
    SerialChannel channel = new SerialManager("/dev/cu.usbmodem1411")
        .connect();
    channel.addInboundFrameInterceptor(new ApplicationUpdateCatcher());
    channel.addInboundFrameInterceptor(new ApplicationCommandHandlerCatcher());

    Thread.sleep(3_600_000);
    System.exit(0);
  }
}
