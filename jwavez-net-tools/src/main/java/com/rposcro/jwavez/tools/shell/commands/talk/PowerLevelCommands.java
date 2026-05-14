package com.rposcro.jwavez.tools.shell.commands.talk;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.powerlevel.PowerLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.powerlevel.PowerLevelReport;
import com.rposcro.jwavez.core.commands.types.PowerLevelCommandType;
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

@org.springframework.shell.core.command.annotation.CommandGroup(name = CommandGroup.GENERIC)
public class PowerLevelCommands {

    @Autowired
    private TalkCommunicationService talkCommunicationService;

    @Autowired
    private JWaveZShellContext shellContext;

    @Autowired
    private PowerLevelCommandBuilder powerLevelCommandBuilder;

    @Command(name = {"powerlevel report", "pl report"}, description = "Request power level report",
        availabilityProvider = "checkPowerLevelAvailability")
    public String executePowerLevelReport(@Option(shortName = 'n', longName = "--node-id", required = true) int nodeId)
            throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1().buildGetCommand();
        PowerLevelReport powerLevelReport = talkCommunicationService.requestTalk(nodeId, command, PowerLevelCommandType.POWER_LEVEL_REPORT);
        return String.format("Power level reported: 0x%02X, timeout is: %s[s]\n", powerLevelReport.getPowerLevel(), powerLevelReport.getTimeout());
    }

    @Command(name = {"powerlevel set", "pl set"}, description = "Power level set request",
        availabilityProvider = "checkPowerLevelAvailability")
    public String executePowerLevelSet(
            @Option(shortName = 'n', longName = "node-id", required = true) int nodeId,
            @Option(shortName = 't', longName = "timeout", required = true) int powerLevelTimeout,
            @Option(shortName = 'v', longName = "power-level", required = true) int powerLevel
    ) throws SerialException {
        ZWaveControlledCommand command = powerLevelCommandBuilder.v1()
                .buildSetCommand((byte) powerLevel, (byte) powerLevelTimeout);
        talkCommunicationService.sendCommand(nodeId, command);
        return String.format("Command %s successfully sent to node %s", PowerLevelCommandType.POWER_LEVEL_SET, nodeId);
    }

    @Bean
    public AvailabilityProvider checkPowerLevelAvailability() {

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
