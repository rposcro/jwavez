package com.rposcro.jwavez.commands.configuration;

import com.rposcro.jwavez.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.commands.supported.ZWaveSupportedCommandResolver;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.utils.PackageScanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SupportedCommandsRegistry {

  private Map<CommandClass, ZWaveSupportedCommandResolver<?>> resolverMap;
  private static SupportedCommandsRegistry instance;

  public static SupportedCommandsRegistry instance() {
    if (instance == null) {
      instance = new SupportedCommandsRegistry();
      instance.initialize();
    }
    return instance;
  }

  private void initialize() {
    resolverMap = new HashMap<>();
    PackageScanner scanner = new PackageScanner();
    List<Class<ZWaveSupportedCommandResolver>> classList = scanner.findAllClassifiedClasses(
        "com.rposcro.jwavez.commands.supported",
        false,
        SupportedCommandResolver.class,
        ZWaveSupportedCommandResolver.class);
    scanner.instantiateAll(classList).stream()
        .collect(Collectors.toMap(ZWaveSupportedCommandResolver::supportedCommandClass, Function.identity()));
  }
}
