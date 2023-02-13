package com.rposcro.jwavez.core.commands.supported.resolvers;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelSupportedScaleReport;
import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelSupportedSensorReport;
import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT;
import static com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType.SENSOR_MULTILEVEL_SUPPORTED_SCALE_REPORT;
import static com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType.SENSOR_MULTILEVEL_SUPPORTED_SENSOR_REPORT;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_SENSOR_MULTILEVEL)
public class SensorMultilevelResolver extends AbstractCommandResolver<SensorMultilevelCommandType> {

    private static Map<SensorMultilevelCommandType, BiFunction<ImmutableBuffer, NodeId, ZWaveSupportedCommand>> suppliersPerCommandType;

    static {
        suppliersPerCommandType = new HashMap<>();
        suppliersPerCommandType.put(SENSOR_MULTILEVEL_REPORT, SensorMultilevelReport::new);
        suppliersPerCommandType.put(SENSOR_MULTILEVEL_SUPPORTED_SENSOR_REPORT, SensorMultilevelSupportedSensorReport::new);
        suppliersPerCommandType.put(SENSOR_MULTILEVEL_SUPPORTED_SCALE_REPORT, SensorMultilevelSupportedScaleReport::new);
    }

    public SensorMultilevelResolver() {
        super(suppliersPerCommandType);
    }
}
