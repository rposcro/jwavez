package com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SWITCH_MULTILEVEL;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_MAXIMUM;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_MINIMUM;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_RESTORE;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_START_LEVEL_CHANGE;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class SwitchMultiLevelCommandBuilderV2 extends SwitchMultiLevelCommandBuilderV1 {

    public ZWaveControlledCommand buildSetCommand(byte value, byte duration) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                value,
                duration);
    }

    public ZWaveControlledCommand buildSetMaximumCommand(byte duration) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_MAXIMUM,
                duration);
    }

    public ZWaveControlledCommand buildSetMinimumCommand(byte duration) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_MINIMUM,
                duration);
    }

    public ZWaveControlledCommand buildSetRestoreCommand(byte duration) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_RESTORE,
                duration);
    }

    public ZWaveControlledCommand buildStartLevelChangeCommand(
            boolean changeDown, boolean ignoreStartLevel, byte startLevel, byte duration) {
        return new ZWaveControlledCommand(
            CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
            SWITCH_MULTILEVEL_START_LEVEL_CHANGE.getCode(),
            (byte) (0x00 | (changeDown ? 0x40 : 0x00) | (ignoreStartLevel ? 0x20 : 0x00)),
            startLevel,
            duration);
    }
}
