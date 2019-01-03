package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CommandTypesRegistry {

  private static Map<CommandClass, Map<Byte, CommandType>> typesPerCode = new HashMap<>();
  private static Map<Class<? extends CommandType>, Map<CommandType, Byte>> codesPerType = new HashMap<>();

  public static <T extends CommandType> T decodeCommandType(CommandClass commandClass, byte code) {
    return CommandTypesRegistry.constantOfCode(commandClass, code);
  }

  static void registerConstant(CommandType constant, int code) {
    Map<Byte, CommandType> codeToTypeMap = typesPerCode.computeIfAbsent(constant.getCommandClass(), (clazz) -> new HashMap<>());
    codeToTypeMap.put((byte) code, constant);

    Map<CommandType, Byte> typeToCodeMap = codesPerType.computeIfAbsent(constant.getClass(), (clazz) -> new HashMap<>());
    typeToCodeMap.put(constant, (byte) code);
  }

  static <T extends CommandType> T constantOfCode(CommandClass commandClass, byte code) {
    Map<Byte, CommandType> typesMap = Optional.ofNullable(typesPerCode.get(commandClass))
        .orElseThrow(() -> new CommandNotSupportedException("Command class " + commandClass + " unsupported!"));
    return (T) Optional.ofNullable(typesMap.get(code)).orElseThrow(() -> new CommandNotSupportedException("Command type code " + code + " unsupported!"));
  }

  static byte codeOfType(CommandType commandType) {
    return codesPerType.get(commandType.getClass()).get(commandType);
  }
}
