package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.commands.enums.ConfigurationCommandType;
import com.rposcro.jwavez.core.commands.supported.configuration.ConfigurationReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_CONFIGURATION)
public class ConfigurationCommandResolver extends AbstractCommandResolver<ConfigurationCommandType> {

  private static Map<ConfigurationCommandType, Function<ImmutableBuffer, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(ConfigurationCommandType.CONFIGURATION_REPORT, ConfigurationReport::new);
  }

  public ConfigurationCommandResolver() {
    super(suppliersPerCommandType.keySet());
  }

  @Override
  public ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer) {
    ConfigurationCommandType commandType = commandTypesRegistry.decodeCommandType(supportedCommandClass(), payloadBuffer.getByte(1));
    Function<ImmutableBuffer, ZWaveSupportedCommand> producer = Optional.ofNullable(suppliersPerCommandType.get(commandType))
        .orElseThrow(() -> new CommandNotSupportedException("Command " + commandType + " has no resolver implemented!"));
    return producer.apply(payloadBuffer);
  }
}
