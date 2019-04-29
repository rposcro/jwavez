package com.rposcro.jwavez.serial.probe.frame;

import com.rposcro.jwavez.serial.probe.frame.constants.FrameCategory;

public class CANFrame extends SerialFrame {

  private static final CANFrame instance = new CANFrame();

  private CANFrame() {
    super(FrameCategory.CAN.getCode());
  }

  public static CANFrame instance() {
    return instance;
  }
}
