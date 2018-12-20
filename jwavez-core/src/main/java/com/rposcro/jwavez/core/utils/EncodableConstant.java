package com.rposcro.jwavez.core.utils;

public interface EncodableConstant {

  default byte getCode() {
    return EncodableConstantsRegistry.codeOfConstant(this);
  }

  default boolean matchesCode(byte code) {
    return EncodableConstantsRegistry.codeOfConstant(this) == code;
  }
}
