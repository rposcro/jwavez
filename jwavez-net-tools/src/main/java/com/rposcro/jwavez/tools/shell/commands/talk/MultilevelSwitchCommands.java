package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.scopes.ShellScope;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup(CommandGroup.TALK)
public class MultilevelSwitchCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder;

    @ShellMethod(value = "Request multilevel report", key = {"switchmultilevel report", "sml report"})
    public String executeMultilevelGet(int nodeId) throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v1().buildGetCommand();
        SwitchMultilevelReport report = talkCommunicationService.requestTalk(nodeId, command, SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT);
        return String.format(String.format("Multilevel report for node %s\nCurrent value: %s, Target value: %s, Duration: %s\n"
                , nodeId, report.getCurrentValue(), report.getTargetValue(), report.getDuration()
        ));
    }

    @ShellMethod(value = "Send multilevel set", key = {"switchmultilevel set", "sml set"})
    public String executeMultilevelSet(int nodeId, int value, @ShellOption(defaultValue = "0") int duration) throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v2().buildSetCommand((byte) value, (byte) duration);
        boolean success = talkCommunicationService.sendCommand(nodeId, command);
        if (success) {
            return String.format("Command %s successfully sent to node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        } else {
            return String.format("Failed to deliver command %s o node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        }
    }

    @ShellMethodAvailability
    public Availability checkAvailability() {

        if (ShellScope.TALK != shellContext.getScopeContext().getScope()) {
            return Availability.unavailable("Command not available in current scope");
        }

        return shellContext.getDongleDevicePath() != null ?
                Availability.available() :
                Availability.unavailable("ZWave dongle device is not specified");
    }

}
