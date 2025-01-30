package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.powerlevel.PowerLevelReport;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
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
public class PowerLevelCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private PowerLevelCommandBuilder powerLevelCommandBuilder;

    @Command(command = "power-level report", alias = "pl report", description = "Request power level report")
    @CommandAvailability(provider = "dongleAvailability")
    public String executePowerLevelReport(@Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1().buildGetCommand();
        PowerLevelReport powerLevelReport = talkCommunicationService.requestTalk(nodeId, command, PowerLevelCommandType.POWER_LEVEL_REPORT);
        return String.format("Power level reported: 0x%02X, timeout is: %s[s]\n", powerLevelReport.getPowerLevel(), powerLevelReport.getTimeout());
    }

    @Command(command = "power-level set", alias = "pl set", description = "Power level set request")
    @CommandAvailability(provider = "dongleAvailability")
    public String executePowerLevelSet(
            @Option(longNames = "node-id", shortNames = 'n', required = true) int nodeId,
            @Option(longNames = "value", shortNames = 'w', required = true) int powerLevel,
            @Option(longNames = "timeout", shortNames = 't', required = true) int powerLevelTimeout
    ) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1()
                .buildSetCommand((byte) powerLevel, (byte) powerLevelTimeout);
        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s", PowerLevelCommandType.POWER_LEVEL_SET, nodeId);
    }
}
