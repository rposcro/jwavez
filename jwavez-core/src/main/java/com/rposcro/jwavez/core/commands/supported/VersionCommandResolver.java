package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import com.rposcro.jwavez.core.commands.supported.version.VersionCommandClassReport;
import com.rposcro.jwavez.core.commands.supported.version.VersionReport;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_VERSION)
public class VersionCommandResolver extends AbstractCommandResolver<VersionCommandType> {

  private static Map<VersionCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(VersionCommandType.VERSION_REPORT, VersionReport::new);
    suppliersPerCommandType.put(VersionCommandType.VERSION_COMMAND_CLASS_REPORT, VersionCommandClassReport::new);
  }

  public VersionCommandResolver() {
    super(suppliersPerCommandType);
  }
}
