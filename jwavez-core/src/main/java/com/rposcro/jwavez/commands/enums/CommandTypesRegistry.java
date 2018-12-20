package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;
import java.util.HashMap;
import java.util.Map;

public final class CommandTypesRegistry {

  private static Map<CommandClass, Map<Byte, CommandType>> typesPerCode = new HashMap<>();
  private static Map<Class<? extends CommandType>, Map<CommandType, Byte>> codesPerType = new HashMap<>();

  public <T extends CommandType> T decodeCommandType(CommandClass commandClass, byte code) {
    return CommandTypesRegistry.constantOfCode(commandClass, code);
  }

  static void registerConstant(CommandType constant, int code) {
    Map<Byte, CommandType> codeToTypeMap = typesPerCode.computeIfAbsent(constant.getCommandClass(), (clazz) -> new HashMap<>());
    codeToTypeMap.put((byte) code, constant);

    Map<CommandType, Byte> typeToCodeMap = codesPerType.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    typeToCodeMap.put(constant, (byte) code);
  }

  static <T extends CommandType> T constantOfCode(CommandClass commandClass, byte code) {
    T constant = (T) typesPerCode.get(commandClass).get(code);
    return constant;
  }

  static byte codeOfConstant(CommandType constants) {
    return codesPerType.get(constants.getClass()).get(constants);
  }
}
