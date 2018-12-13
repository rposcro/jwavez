package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.commands.enums.CommandTypeResolver;
import com.rposcro.jwavez.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.enums.CommandClass;
import com.rposcro.jwavez.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_CONFIGURATION)
public class ConfigurationCommandResolver implements ZWaveCommandResolver {

  private static Map<ConfigurationCommandType, Function<ImmutableBuffer, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(ConfigurationCommandType.CONFIGURATION_REPORT, ConfigurationReport::new);
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer) {
    ConfigurationCommandType commandType = CommandTypeResolver.constantOfCode(ConfigurationCommandType.class, payloadBuffer.getByte(1));
    Function<ImmutableBuffer, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new IllegalArgumentException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer);
  }
}
