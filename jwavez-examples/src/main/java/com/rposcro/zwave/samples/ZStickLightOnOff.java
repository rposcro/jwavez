package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.frame.requests.ZStickSetConfigRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.ZStickSetConfigResponseFrame;
import com.rposcro.jwavez.serial.transactions.SerialTransaction;
import com.rposcro.jwavez.serial.transactions.SimpleRequestResponseTransaction;

public class ZStickLightOnOff extends AbstractExample {

  public ZStickLightOnOff() {
    super("/dev/cu.usbmodem1411");
  }

  /**
   * Turns ZStick led off
   */
  private void turnLedOff() throws Exception {
    SerialTransaction<ZStickSetConfigResponseFrame> transaction = new SimpleRequestResponseTransaction<>(
        ZStickSetConfigRequestFrame.builder().ledIndicator(false).build(),
        ZStickSetConfigResponseFrame.class);
    channel.executeTransaction(transaction).get();
  }

  /**
   * Turns ZStick led on
   */
  private void turnLedOn() throws Exception {
    SerialTransaction<ZStickSetConfigResponseFrame> transaction = new SimpleRequestResponseTransaction(
        ZStickSetConfigRequestFrame.builder().ledIndicator(true).build(),
        ZStickSetConfigResponseFrame.class);
    channel.executeTransaction(transaction).get();
  }

  public static void main(String[] args) throws Exception {
    ZStickLightOnOff test = new ZStickLightOnOff();
    test.turnLedOff();
    //test.turnLedOn();
  }
}
