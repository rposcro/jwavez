package com.rposcro.jwavez.serial.probe.zstick;

import lombok.Getter;

public enum ZStickConfigParameter {

  USB_LED_INDICATOR(0x51),
  SECURITY_MODE(0xF2),
  SECURITY_KEY(0xF3),
  RF_POWER_LEVEL(0xDC),
  LOCK(0xFC)
  ;

  @Getter
  private byte parameterCode;

  private ZStickConfigParameter(int parameterCode) {
    this.parameterCode = (byte) parameterCode;
  }

}
