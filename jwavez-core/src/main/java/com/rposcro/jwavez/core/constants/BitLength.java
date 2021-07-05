package com.rposcro.jwavez.core.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BitLength {

  BIT_LENGTH_8(8),
  BIT_LENGTH_16(16),
  BIT_LENGTH_32(32),
  ;

  @Getter
  private int bitsNumber;

  public int getBytesNumber() {
    return bitsNumber / 8;
  }

  public static BitLength ofBytesNumber(int bytesNumber) {
    switch(bytesNumber) {
      case 1:
        return BIT_LENGTH_8;
      case 2:
        return BIT_LENGTH_16;
      case 4:
        return BIT_LENGTH_32;
      default:
        throw new IllegalArgumentException("Unsupported number of bytes: " + bytesNumber);
    }
  }
}
