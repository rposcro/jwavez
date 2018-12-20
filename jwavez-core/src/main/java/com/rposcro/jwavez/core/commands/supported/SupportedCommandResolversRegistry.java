package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.utils.PackageScanner;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupportedCommandResolversRegistry {

  private static SupportedCommandResolversRegistry instance;

  private Map<CommandClass, ZWaveSupportedCommandResolver> resolverMap;

  public ZWaveSupportedCommandResolver findResolver(CommandClass commandClass) {
    return Optional.ofNullable(resolverMap.get(commandClass))
        .orElseThrow(() -> new CommandNotSupportedException("Command class " + commandClass + " is not supported yet!"));
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
    List<Class<ZWaveSupportedCommandResolver>> classList = scanner.findAllClassifiedClasses(
        "com.rposcro.jwavez.core.commands.supported",
        false,
        SupportedCommandResolver.class,
        ZWaveSupportedCommandResolver.class);
    List<ZWaveSupportedCommandResolver> resolverList = scanner.instantiateAll(classList);
    resolverMap = resolverList.stream()
        .collect(Collectors.toMap(ZWaveSupportedCommandResolver::supportedCommandClass, Function.identity()));
  }
}
