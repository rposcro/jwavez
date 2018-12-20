package com.rposcro.jwavez.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class EncodableConstantsRegistry {

  private static Map<Class<? extends EncodableConstant>, Map<Byte, EncodableConstant>> constantsPerCode = new HashMap<>();
  private static Map<Class<? extends EncodableConstant>, Map<EncodableConstant, Byte>> codesPerConstant = new HashMap<>();

  public static void registerConstant(EncodableConstant constant, byte code) {
    Map<Byte, EncodableConstant> constantsMap = constantsPerCode.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    constantsMap.put(code, constant);
    Map<EncodableConstant, Byte> codesMap = codesPerConstant.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    codesMap.put(constant, code);
  }

  public static <T extends EncodableConstant> T constantOfCode(Class<T> clazz, byte code) {
    return optionalConstantOfCode(clazz, code)
        .orElseThrow(() -> new IllegalArgumentException("No constant of class " + clazz + " registered for code " + code));
  }

  public static <T extends EncodableConstant> Optional<T> optionalConstantOfCode(Class<T> clazz, byte code) {
    return Optional.ofNullable((T) constantsPerCode.get(clazz).get(code));
  }

  public static byte codeOfConstant(EncodableConstant constant) {
    Byte code = codesPerConstant.get(constant.getClass()).get(constant);
    if (constant == null) {
      throw new IllegalArgumentException("No code " + code + " registered for constant class " + constant.getClass());
    }
    return code;
  }
}
