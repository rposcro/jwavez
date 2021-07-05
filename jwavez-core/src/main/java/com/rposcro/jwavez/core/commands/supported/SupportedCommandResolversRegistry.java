package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.utils.PackageScanner;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupportedCommandResolversRegistry {

  private static SupportedCommandResolversRegistry instance;

  private Map<CommandClass, ZWaveSupportedCommandResolver> resolverMap;

  public ZWaveSupportedCommandResolver findResolver(CommandClass commandClass) {
    return Optional.ofNullable(resolverMap.get(commandClass))
        .orElseThrow(() -> new CommandNotSupportedException(commandClass));
  }

  public static SupportedCommandResolversRegistry instance() {
    if (instance == null) {
      instance = new SupportedCommandResolversRegistry();
      instance.initialize();
    }
    return instance;
  }

  private void initialize() {
    PackageScanner scanner = new PackageScanner();
    Set<Class<? extends ZWaveSupportedCommandResolver>> classList = scanner.findAllClassifiedClasses(
        SupportedCommandResolver.class,
        ZWaveSupportedCommandResolver.class,
        "com.rposcro.jwavez.core.commands.supported");
    Set<ZWaveSupportedCommandResolver> resolverList = scanner.instantiateAll(classList);
    resolverMap = resolverList.stream()
        .collect(Collectors.toMap(ZWaveSupportedCommandResolver::supportedCommandClass, Function.identity()));
    log.debug("Class registry: {} resolver classes", resolverMap.size());
  }
}
