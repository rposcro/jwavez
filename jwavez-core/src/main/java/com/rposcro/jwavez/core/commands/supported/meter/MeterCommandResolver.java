package com.rposcro.jwavez.core.commands.supported.meter;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.supported.SupportedCommandResolver;
import com.rposcro.jwavez.core.commands.supported.meter.MeterReport;
import com.rposcro.jwavez.core.commands.supported.resolvers.AbstractCommandResolver;
import com.rposcro.jwavez.core.commands.types.MeterCommandType;

@SupportedCommandResolver(commandClass = CommandClass.CMD_CLASS_METER)
public class MeterCommandResolver extends AbstractCommandResolver<MeterCommandType> {

    public MeterCommandResolver() {
        addSupplier(MeterCommandType.METER_REPORT, MeterReport::new);
    }
}
