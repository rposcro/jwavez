package com.rposcro.zwave.samples;

import com.rposcro.jwavez.serial.frame.requests.ZStickSetConfigRequestFrame;

public class ZStickLightOnOff extends AbstractExample {

  public ZStickLightOnOff() {
    super("/dev/cu.usbmodem1411");
  }

  /**
   * Turns ZStick led off
   */
  private void turnLedOff() throws Exception {
    channel.sendFrameWithResponseAndWait(ZStickSetConfigRequestFrame.builder().ledIndicator(false).build());
  }

  /**
   * Turns ZStick led on
   */
  private void turnLedOn() throws Exception {
    channel.sendFrameWithResponseAndWait(ZStickSetConfigRequestFrame.builder().ledIndicator(true).build());
  }

  public static void main(String[] args) throws Exception {
    ZStickLightOnOff test = new ZStickLightOnOff();
    test.turnLedOff();
    //test.turnLedOn();
    System.exit(0);
  }
}
