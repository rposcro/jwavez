package com.rposcro.jwavez.core.constants;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

import java.util.Optional;

public enum MeterType implements EncodableConstant {

  ELECTRIC_METER(1),
  GAS_METER(2),
  WATER_METER(3),
  HEATING_METER(4),
  COOLING_METER(5),
  ;

  MeterType(int code) {
    EncodableConstantsRegistry.registerConstant(this, (byte) code);
  }

  public static MeterType ofCode(byte code) {
    return EncodableConstantsRegistry.constantOfCode(MeterType.class, code);
  }

  public static Optional<MeterType> ofCodeOptional(byte code) {
    return EncodableConstantsRegistry.optionalConstantOfCode(MeterType.class, code);
  }
}
