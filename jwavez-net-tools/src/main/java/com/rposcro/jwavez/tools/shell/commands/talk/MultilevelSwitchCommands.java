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
import org.springframework.context.annotation.Bean;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.TALK)
public class MultilevelSwitchCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder;

    @Command(name = "switchmultilevel report", alias = "sml report", description = "Request multilevel report",
            availabilityProvider = "multilevelSwitchCommandsAvailability")
    public String executeMultilevelGet(int nodeId) throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v1().buildGetCommand();
        SwitchMultilevelReport report = talkCommunicationService.requestTalk(nodeId, command, SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT);
        return String.format(String.format("Multilevel report for node %s\nCurrent value: %s, Target value: %s, Duration: %s\n"
                , nodeId, report.getCurrentValue(), report.getTargetValue(), report.getDuration()
        ));
    }

    @Command(name = "switchmultilevel set", alias = "sml set", description = "Send multilevel set",
            availabilityProvider = "multilevelSwitchCommandsAvailability")
    public String executeMultilevelSet(int nodeId, int value, @Option(longName = "duration", defaultValue = "0") int duration)
            throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v2().buildSetCommand((byte) value, (byte) duration);
        boolean success = talkCommunicationService.sendCommand(nodeId, command);
        if (success) {
            return String.format("Command %s successfully sent to node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        } else {
            return String.format("Failed to deliver command %s o node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        }
    }

    @Bean
    public AvailabilityProvider multilevelSwitchCommandsAvailability() {
        Availability availability;

        if (ShellScope.TALK != shellContext.getScopeContext().getScope()) {
            availability = Availability.unavailable("Command not available in current scope");
        } else {
            availability = shellContext.getDongleDevicePath() != null ?
                    Availability.available() :
                    Availability.unavailable("ZWave dongle device is not specified");
        }

        return AvailabilityProvider.of(availability);
    }

}
