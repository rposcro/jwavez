package com.rposcro.jwavez.serial.probe.frame;

import com.rposcro.jwavez.serial.probe.frame.constants.FrameCategory;

public class NAKFrame extends SerialFrame {

  private static final NAKFrame instance = new NAKFrame();

  private NAKFrame() {
    super(FrameCategory.NAK.getCode());
  }

  public static NAKFrame instance() {
    return instance;
  }
}
