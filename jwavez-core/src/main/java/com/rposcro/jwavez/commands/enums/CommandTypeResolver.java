package com.rposcro.jwavez.commands.enums;

public final class CommandTypeResolver {

  public static <T> T constantOfCode(Class<T> clazz, byte code) {
    return CommandConstantsRegistry.constantOfCode(clazz, code);
  }
}
