package com.rposcro.jwavez.commands.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
abstract class CommandConstantsRegistry {

  private static Map<Class<? extends CommandTypeEnum>, Map<Byte, CommandTypeEnum>> constantsPerCode = new HashMap<>();
  private static Map<Class<? extends CommandTypeEnum>, Map<CommandTypeEnum, Byte>> codesPerConstant = new HashMap<>();

  static void registerConstant(CommandTypeEnum constant, int code) {
    Map<Byte, CommandTypeEnum> codeMap = constantsPerCode.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    codeMap.put((byte) code, constant);
    Map<CommandTypeEnum, Byte> constantMap = codesPerConstant.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    constantMap.put(constant, (byte) code);
  }

  static <T> T constantOfCode(Class<T> clazz, byte code) {
    T constant = (T) constantsPerCode.get(clazz).get(code);
    return constant;
  }

  static byte codeOfConstant(CommandTypeEnum constants) {
    return codesPerConstant.get(constants.getClass()).get(constants);
  }
}
