package com.rposcro.jwavez.serial.model;

public class RxStatus {

  private boolean routedBusy;
  private boolean lowPower;
  private boolean exploreFrame;
  private boolean foreignFrame;
  private boolean foreignHomeId;
  private FrameCast frameCast;

  public RxStatus(byte status) {
    routedBusy = (status & 0x01) > 0;
    lowPower = (status & 0x02) > 0;
    exploreFrame = (status & 0x18) == 0x10;
    foreignFrame = (status & 0x40) > 0;
    foreignHomeId = (status & 0x80) > 0;

    switch(status & 0x0c) {
      case 0x00:
        frameCast = FrameCast.SINGLE;
        break;
      case 0x04:
        frameCast = FrameCast.BROADCAST;
        break;
      case 0x08:
        frameCast = FrameCast.MULTICAST;
        break;
    }
  }
}
