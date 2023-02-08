package com.rposcro.jwavez.core.commands.controlled.builders.switchbinary;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class SwitchBinaryCommandBuilderV2 extends SwitchBinaryCommandBuilderV1 {

    public ZWaveControlledCommand buildSetCommandV2(byte value, byte duration) {
        return new ZWaveControlledCommand(
                CommandClass.CMD_CLASS_SWITCH_BINARY.getCode(),
                SwitchBinaryCommandType.BINARY_SWITCH_SET.getCode(),
                value,
                duration);
    }
}
