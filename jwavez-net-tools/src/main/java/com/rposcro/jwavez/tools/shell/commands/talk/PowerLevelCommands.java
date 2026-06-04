package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.powerlevel.PowerLevelReport;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import com.rposcro.jwavez.tools.shell.services.TalkCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.GENERIC)
public class PowerLevelCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private PowerLevelCommandBuilder powerLevelCommandBuilder;

    @Command(name = {"power-level report", "pl report"}, description = "Request power level report",
        availabilityProvider = "dongleAvailability")
    public String executePowerLevelReport(
            @Argument(index = 0, description = "Node id to send the power level report request to") int nodeId)
            throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1().buildGetCommand();
        PowerLevelReport powerLevelReport = talkCommunicationService.requestTalk(nodeId, command, PowerLevelCommandType.POWER_LEVEL_REPORT);
        return String.format("Power level reported: 0x%02X, timeout is: %s[s]\n", powerLevelReport.getPowerLevel(), powerLevelReport.getTimeout());
    }

    @Command(name = {"power-level set", "pl set"}, description = "Power level set request",
        availabilityProvider = "dongleAvailability")
    public String executePowerLevelSet(
            @Argument(index = 0, description = "Node id where the power level should be set") int nodeId,
            @Argument(index = 1, description = "Power level value") int powerLevel,
            @Option(shortName = 't', longName = "timeout", required = true) int powerLevelTimeout
    ) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1()
                .buildSetCommand((byte) powerLevel, (byte) powerLevelTimeout);
        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s", PowerLevelCommandType.POWER_LEVEL_SET, nodeId);
    }
}
