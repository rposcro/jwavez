package com.rposcro.jwavez.serial.frame.contants;

import com.rposcro.jwavez.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.utils.EncodableConstant;

public enum TransmitCompletionStatus implements EncodableConstant {

  TRANSMIT_COMPLETE_OK(0x00),
  TRANSMIT_COMPLETE_NO_ACK(0x01),
  TRANSMIT_COMPLETE_FAIL(0x02),
  TRANSMIT_COMPLETE_NO_IDLE(0x03),
  TRANSMIT_NO_ROUTE(0x04),
  ;

  private TransmitCompletionStatus(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static TransmitCompletionStatus ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(TransmitCompletionStatus.class, code);
  }
}
