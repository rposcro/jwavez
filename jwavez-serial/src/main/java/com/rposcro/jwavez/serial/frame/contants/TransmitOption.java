package com.rposcro.jwavez.serial.frame.contants;

import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;
import com.rposcro.jwavez.core.utils.EncodableConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public enum TransmitOption implements EncodableConstant {

  TRANSMIT_OPTION_ACK(0x01),
  TRANSMIT_OPTION_LOW_POWER(0x02),
  TRANSMIT_OPTION_AUTO_ROUTE(0x04),
  TRANSMIT_OPTION_NO_ROUTE(0x10),
  TRANSMIT_OPTION_EXPLORE(0x20),
  ;

  private TransmitOption(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static TransmitOption ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(TransmitOption.class, code);
  }

  public static Builder optionsBuilder() {
    return new Builder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private byte options;

    public Builder withOption(TransmitOption option) {
      options |= option.getCode();
      return this;
    }

    public byte build() {
      return options;
    }
  }
}
