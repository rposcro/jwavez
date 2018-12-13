package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.frame.contants.FrameCategory;

public class CANFrame extends SerialFrame {

  private static final CANFrame instance = new CANFrame();

  private CANFrame() {
    super(FrameCategory.CAN.getCode());
  }

  public static CANFrame instance() {
    return instance;
  }
}
