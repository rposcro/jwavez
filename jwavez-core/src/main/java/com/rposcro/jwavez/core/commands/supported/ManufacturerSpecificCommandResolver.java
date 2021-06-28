package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.commands.enums.ManufacturerSpecificCommandType;
import com.rposcro.jwavez.core.commands.supported.manufacturerspecific.ManufacturerSpecificReport;
import com.rposcro.jwavez.core.enums.CommandClass;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_MANUFACTURER_SPECIFIC)
public class ManufacturerSpecificCommandResolver extends AbstractCommandResolver<ManufacturerSpecificCommandType> {

  private static Map<ManufacturerSpecificCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

  static {
    suppliersPerCommandType = new HashMap<>();
    suppliersPerCommandType.put(ManufacturerSpecificCommandType.MANUFACTURER_SPECIFIC_REPORT, ManufacturerSpecificReport::new);
  }

  public ManufacturerSpecificCommandResolver() {
    super(suppliersPerCommandType);
  }
}
