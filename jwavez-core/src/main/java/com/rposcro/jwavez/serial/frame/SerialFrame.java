package com.rposcro.jwavez.serial.frame;

public abstract class SerialFrame {

  public static final ACKFrame ACK_FRAME = ACKFrame.instance();
  public static final NAKFrame NAK_FRAME = NAKFrame.instance();
  public static final CANFrame CAN_FRAME = CANFrame.instance();

  private byte[] buffer;

  protected SerialFrame(byte... buffer) {
    setBuffer(buffer);
  }

  protected SerialFrame() {}

  protected void setBuffer(byte... buffer) {
    this.buffer = buffer;
  }

  public byte[] getBuffer() {
    return buffer;
  }
}
