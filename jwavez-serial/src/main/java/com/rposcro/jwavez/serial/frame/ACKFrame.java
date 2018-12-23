package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.frame.constants.FrameCategory;

public class ACKFrame extends SerialFrame {

  private static final ACKFrame instance = new ACKFrame();

  private ACKFrame() {
    super(FrameCategory.ACK.getCode());
  }

  public static ACKFrame instance() {
    return instance;
  }
}
