package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@Command(group = CommandGroup.TALK)
public class MultilevelSwitchCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private SwitchMultiLevelCommandBuilder switchMultiLevelCommandBuilder;

    @Command(command = "switch-multilevel report", alias = "sml report", description = "Request multilevel report")
    @CommandAvailability(provider = "dongleAvailability")
    public String executeMultilevelGet(@Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId) throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v1().buildGetCommand();
        SwitchMultilevelReport report = talkCommunicationService.requestTalk(nodeId, command, SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT);
        return String.format(String.format("Multilevel report for node %s\nCurrent value: %s, Target value: %s, Duration: %s\n"
                , nodeId, report.getCurrentValue(), report.getTargetValue(), report.getDuration()
        ));
    }

    @Command(command = "switch-multilevel set", alias = "sml set", description = "Send multilevel set")
    @CommandAvailability(provider = "dongleAvailability")
    public String executeMultilevelSet(
        @Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId,
        @Option(longNames = "value", shortNames = 'w', required = true) int value,
        @Option(longNames = "duration", defaultValue = "0") int duration) throws SerialException {
        ZWaveControlledCommand command = switchMultiLevelCommandBuilder.v2().buildSetCommand((byte) value, (byte) duration);
        boolean success = talkCommunicationService.sendCommand(nodeId, command);
        if (success) {
            return String.format("Command %s successfully sent to node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        } else {
            return String.format("Failed to deliver command %s o node %s", SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_SET, nodeId);
        }
    }
}
