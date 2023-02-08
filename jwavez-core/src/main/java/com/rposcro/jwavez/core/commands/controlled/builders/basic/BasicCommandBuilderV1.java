package com.rposcro.jwavez.core.commands.controlled.builders.basic;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class BasicCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand() {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_BASIC.getCode(),
                BasicCommandType.BASIC_GET.getCode()
        );
    }

    public ZWaveControlledCommand buildSetCommand(byte value) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_BASIC.getCode(),
                BasicCommandType.BASIC_SET.getCode(),
                value
        );
    }
}
