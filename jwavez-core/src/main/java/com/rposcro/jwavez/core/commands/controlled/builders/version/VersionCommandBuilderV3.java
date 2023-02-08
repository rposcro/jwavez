package com.rposcro.jwavez.core.commands.controlled.builders.version;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.VersionCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class VersionCommandBuilderV3 extends VersionCommandBuilderV1 {

    public ZWaveControlledCommand buildCapabilitiesGetCommand() {
        return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_CAPABILITIES_GET.getCode());
    }

    public ZWaveControlledCommand buildZWaveSoftwareGetVersion() {
        return new ZWaveControlledCommand(CommandClass.CMD_CLASS_VERSION.getCode(), VersionCommandType.VERSION_ZWAVE_SOFTWARE_GET.getCode());
    }
}
