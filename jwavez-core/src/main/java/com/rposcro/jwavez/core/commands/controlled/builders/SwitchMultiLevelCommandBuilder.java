package com.rposcro.jwavez.core.commands.controlled.builders;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;

import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.*;
import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SWITCH_MULTILEVEL;

public class SwitchMultiLevelCommandBuilder {

    public ZWaveControlledCommand buildGetCommand() {
        return new ZWaveControlledCommand(CMD_CLASS_SWITCH_MULTILEVEL.getCode(), SWITCH_MULTILEVEL_GET.getCode());
    }

    public ZWaveControlledCommand buildSetCommand(byte value, byte duration) {
        return new ZWaveControlledCommand(CMD_CLASS_SWITCH_MULTILEVEL.getCode(), SWITCH_MULTILEVEL_SET.getCode(), value, duration);
    }

    public ZWaveControlledCommand buildStartLevelChangeCommand(boolean changeDown, boolean ignoreStartLevel, byte startLevel, byte duration) {
        return new ZWaveControlledCommand(
            CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
            SWITCH_MULTILEVEL_START_LEVEL_CHANGE.getCode(),
            (byte) (0x00 | (changeDown ? 0x40 : 0x00) | (ignoreStartLevel ? 0x20 : 0x00)),
            startLevel,
            duration);
    }

    public ZWaveControlledCommand buildStopLevelChangeCommand() {
        return new ZWaveControlledCommand(
            CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
            SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE.getCode());
    }
}
