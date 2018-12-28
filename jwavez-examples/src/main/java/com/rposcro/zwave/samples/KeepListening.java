package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.interceptors.ApplicationCommandHandlerLogger;
import com.rposcro.jwavez.serial.interceptors.ApplicationUpdateLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * Just waits for incoming communication for an hour
 */
@Slf4j
public class KeepListening extends AbstractExample {

  public KeepListening() {
    super("/dev/cu.usbmodem1411", new ApplicationUpdateLogger(), new ApplicationCommandHandlerLogger());
  }

  public static void main(String[] args) throws Exception {
    KeepListening listener = new KeepListening();
    Thread.sleep(3_600_000);
    System.exit(0);
  }
}
