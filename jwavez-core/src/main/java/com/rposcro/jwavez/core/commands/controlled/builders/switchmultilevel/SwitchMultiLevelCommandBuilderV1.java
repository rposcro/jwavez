package com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.rposcro.jwavez.core.classes.CommandClass.CMD_CLASS_SWITCH_MULTILEVEL;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_MAXIMUM;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_MINIMUM;
import static com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelConstants.VALUE_RESTORE;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_GET;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_START_LEVEL_CHANGE;
import static com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE;

@NoArgsConstructor(access = AccessLevel.MODULE)
public class SwitchMultiLevelCommandBuilderV1 {

    public ZWaveControlledCommand buildGetCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_GET.getCode());
    }

    public ZWaveControlledCommand buildSetCommand(byte value) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                value);
    }

    public ZWaveControlledCommand buildSetMaximumCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_MAXIMUM);
    }

    public ZWaveControlledCommand buildSetMinimumCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_MINIMUM);
    }

    public ZWaveControlledCommand buildSetRestoreCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_SET.getCode(),
                VALUE_RESTORE);
    }

    public ZWaveControlledCommand buildStartLevelChangeCommand(boolean changeDown, boolean ignoreStartLevel, byte startLevel) {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_START_LEVEL_CHANGE.getCode(),
                (byte) (0x00 | (changeDown ? 0x40 : 0x00) | (ignoreStartLevel ? 0x20 : 0x00)),
                startLevel);
    }

    public ZWaveControlledCommand buildStopLevelChangeCommand() {
        return new ZWaveControlledCommand(
                CMD_CLASS_SWITCH_MULTILEVEL.getCode(),
                SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE.getCode());
    }
}
